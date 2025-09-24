# -*- coding: utf-8 -*-
'''
Created on 06.02.2019

@author: boerje
'''

from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.configuration import conf
from datetime import datetime, timedelta
from typing import *

import requests
import json

JAR_LOC = "/usr/local/airflow/projects/marketing-data-import/target"

def slack_notification_script(context):
    """
    Slack-Notification function to be called if a script fails
    """
    # Get hook url
    url = "https://hooks.slack.com/services/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"

    # Get Airflow Variables
    dag_id = context["dag"].dag_id
    task_id = context["task"].task_id
    exec_date = str(context["execution_date"])[:-6]
    base_url = conf.get("webserver", "base_url")
    dag_url = f"{base_url}/graph?dag_id={dag_id}"
    log_url = f"{base_url}/log?dag_id={dag_id}&task_id={task_id}&execution_date={exec_date}"

    # Construct alert message block
    alert_msg = "\n:fire: IMPORT Failed"
    alert_msg += f"""
        *Dag*: <{dag_url}|{dag_id}>
        *Task*: {task_id}  
        *Execution Time*: {exec_date}  
        <{log_url}|Logs> 
    """

    # Construct error block
    msg = {
        "blocks": [
            {
                "type": "section",
                "text": {"type": "mrkdwn", "text": alert_msg}
            }
        ]
    }

    # Send message to Slack
    return requests.post(url, data=json.dumps(msg), timeout = 10)


# these args will get passed on to each operator
# you can override them on a per-task basis during operator initialization
default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2019,2,6,6,0),
    'email': ['boerje@flaregames.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 3,
    'retry_delay': timedelta(minutes=30),
    'execution_timeout' : timedelta(hours=24),
    'on_failure_callback': slack_notification_script,
    #'on_retry_callback': slackNotification_ScriptRetry,
}


class DagBuilder:
    def __init__(self,id: str, descr:str, *,retry: Tuple[int,int] = (3,30), schedule: str):

        if not schedule is None:
            hour,minute = tuple(schedule.split(":"))
            cron = f'{minute} {hour} * * *'
        else:
            cron = None

        self.dag = DAG(
            f"IMPORT_{id}",
            description=descr,
            schedule=cron,
            max_active_tasks=1,
            default_args=default_args | { 'retries': retry[0],'retry_delay': timedelta(minutes=retry[1])} ,
            catchup=False, auto_register=True)

    def __enter__(self):
        self.dag.__enter__()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.dag.__exit__(exc_type, exc_val, exc_tb)
        pass

    def add_task(self, id: str, clazz: str):
        return BashOperator(
            task_id=id,
            bash_command=f'java -classpath {JAR_LOC}/marketing-data-import-new-etl-0.1.jar -Duser.timezone=UTC {clazz} LIVE',
            dag=self.dag)


# with DagBuilder("Adjust_Cohorts",'Daily Adjust Agg & Cohorts',retry=(3,30),schedule="01:30") as b:
#     t0 = b.add_task("Adjust_Agg","com.flaregames.imports.adjust.reports.AdjustKpiImport")
#     t1 = b.add_task("Adjust_DB_Creatives","com.flaregames.imports.facebook.metadata.ImportMetadata")
#     t0 >> t1

with DagBuilder("Adjust_Cohorts_v2",'Daily Adjust Agg & Cohorts V2',retry=(3,30),schedule="00:30") as b:
    t1 = b.add_task("Adjust_Cohorts","com.flaregames.imports.adjust.report_api.ReportApiCohortImport")
    t2 = b.add_task("Adjust_DB_Creatives","com.flaregames.imports.facebook.metadata.ImportMetadata")
    t1 >> t2

#with DagBuilder("Adjust_Cohorts_Backfill",'Daily Adjust Agg & Cohorts V2',retry=(3,30),schedule=None) as b:
#    t1 = b.add_task("Adjust_Cohorts","com.flaregames.imports.adjust.report_api.ReportApiCohortBackfill")

with DagBuilder("Adjust_Emhq_Raw",'Hourly Emhq Raw Events',retry=(1,5),schedule="*:30") as b:
    b.add_task("Adjust_Emhq_Raw","com.flaregames.imports.adjust.accounts.EmhqAdjustImport")

with DagBuilder("Adjust_Hourly_Import",'Hourly import of RAW S3 data from Adjust',retry=(3,15),schedule="*:20") as b:
    b.add_task("Adjust_EMOP","com.flaregames.imports.adjust.AdjustEmhqOperatorImport")
    b.add_task("Adjust_CrushCrush","com.flaregames.imports.adjust.AdjustCrushCrushImport")
    b.add_task("Adjust_BlushBlush","com.flaregames.imports.adjust.AdjustBlushBlushImport")
    b.add_task("Adjust_Rrs","com.flaregames.imports.adjust.AdjustRrsImport")
    b.add_task("Adjust_Zombigunship","com.flaregames.imports.adjust.AdjustZgsImport")
    b.add_task("Adjust_Nonstopnext","com.flaregames.imports.adjust.AdjustNsk2Import")
    b.add_task("Adjust_GCG","com.flaregames.imports.adjust.AdjustGCGImport")
    b.add_task("Adjust_Combatcards","com.flaregames.imports.adjust.AdjustWccImport")
    b.add_task("Adjust_Peak","com.flaregames.imports.adjust.AdjustPeakImport")
    b.add_task("Adjust_Nox","com.flaregames.imports.adjust.AdjustNoxImport")

with DagBuilder("Appsflyer_Hourly","Hourly import of RAW S3 data from Appsflyer",retry=(3,15),schedule="*:15") as b:
    b.add_task("Appsflyer_Hourly","com.flaregames.tech.appsflyer.AFDataLockerLoader")

with DagBuilder("Appsflyer_DataLocker_Ext_Tables","Daily s3 sync datalocke files & ext tablesr",retry=(3,60),schedule="6:30") as b:
    b.add_task("Mobirate_Ext_Tables","com.flaregames.imports.appsflyer.datalocker.MobirateDataLocker")

#with DagBuilder("AdMob_Import","Daily import of AdMob",retry=(4,60),schedule="01:15") as b:
#    b.add_task("AdMob_Import","com.flaregames.tech.admob.AdMopImport")

with DagBuilder("AppleSearchAds_Import","Daily import of AppleSearchAds",retry=(1,60),schedule="02:30") as b:
    t1 = b.add_task("AppleSearchAds_SmileyGamer","com.flaregames.tech.applesearchads.ASAImportSmilyGamer")
    t2 = b.add_task("AppleSearchAds_Emhq","com.flaregames.tech.applesearchads.ASAImportPromotionsoft")
    t3 = b.add_task("AppleSearchAds_Phoenix","com.flaregames.tech.applesearchads.ASAImportLighthouse")
    mark = b.add_task("AppleSearchAds_MarkSuccess","com.flaregames.tech.applesearchads.ASAImportMarkSuccess")
    [t1,t2,t3] >> mark

with DagBuilder("Appslovin_Costs","Daily import of Appslovin Costs",retry=(4,30),schedule="02:00") as b:
    b.add_task("Appslovin_Costs","com.flaregames.imports.applovin.ApplovinImport")
    b.add_task("Appslovin_Campaign","com.flaregames.imports.applovin.campaign.ALCampaignImport")

with DagBuilder("Appslovin_MAX","Daily import of Appslovin MAX Ad Revenue",retry=(4,30),schedule="09:00") as b:
    b.add_task("Appslovin_MAX","com.flaregames.imports.applovin.ApplovinMaxImport")

with DagBuilder("Misc_AdRevenue","Daily import of ad revenue from Appsflyer and AdInMo",retry=(4,30),schedule="12:15") as b:
    # b.add_task("Appfigure_AdRevenue","com.flaregames.imports.appfigures.AppfigureImport")
    # b.add_task("Appsflyer_AdRevenue","com.flaregames.tech.appsflyer.AFDailyAdRevenueImport")
    b.add_task("AdInMo_Placements","com.flaregames.imports.adinmo.AdInMoImport")
    b.add_task("DigTurb_UserRevenue","com.flaregames.imports.digitalturbine.DTUserLevelImport")
    b.add_task("DigTurb_Mediation","com.flaregames.imports.digitalturbine.DTReportImport")
    b.add_task("AdMob_Mediation_v2","com.flaregames.imports.admob.AdMobImport")

with DagBuilder("Appsflyer_Cohorts","Daily import Appsflyer Pull API Cohorts",retry=(4,60),schedule="02:00") as b:
    b.add_task("Appsflyer_Cohorts","com.flaregames.imports.appsflyer.AFCohortImport")

with DagBuilder("Appsflyer_SkAdNetwork","Daily import of RAW S3 SkAdNetwrok data from Appsflyer",retry=(4,60),schedule="13:15") as b:
    b.add_task("Appsflyer_SkAdNetwork","com.flaregames.tech.appsflyer.skadnetwork.AFSkAdNetworkImport")

with DagBuilder("Currencies","Daily Currency Import",retry=(10,10),schedule="00:10") as b:
    t0 = b.add_task("Currencies","com.flaregames.imports.currency.CurrencyBackfill")
    t1 = b.add_task("Currency_Push_Nox","com.flaregames.imports.currency.CurrencyPush")
    t0 >> t1

with DagBuilder("Facebook_Breakdowns","Daily Facebook Breakdowns",retry=(3,30),schedule="02:10") as b:
    b.add_task("Facebook_Breakdowns","com.flaregames.imports.facebook.insights.ImportBreakdowns")

with DagBuilder("GoogleAds","Daily import of Google Ad Words",retry=(4,20),schedule="03:00") as b:
    b.add_task("GoogleAds","com.flaregames.imports.googleads.GoogleAdsImport")

with DagBuilder("IronSource","Daily import of IronSource/Tapjoy Ad Revenue",retry=(4,30),schedule="03:15") as b:
    t0 = b.add_task("IronSource_AdRevenue","com.flaregames.tech.ironsource_v2.IronsourceImport")
    t1 = b.add_task("IronSource_Advertiser_Report","com.flaregames.imports.ironsource.IronsourceImport")
    _  = b.add_task("Tapjoy_AdRevenue","com.flaregames.imports.tapjoy.TapjoyImport")
    t0 >> t1

with DagBuilder("IronSource_IL","Daily import of IronSource impression Level Ad Revenue (V2)",retry=(4,30),schedule="14:30") as b:
    b.add_task("IronSource_IL","com.flaregames.imports.ironsource.IronsourceUserLevelImport")

with DagBuilder("Misc_Spend","Misc daily spend Imports",retry=(4,30),schedule="02:30") as b:
#    b.add_task("Snapchat","com.flaregames.imports.snapchat.SnapchatImport")
#    b.add_task("Twitter","com.flaregames.imports.twitter.TwitterImport")
    b.add_task("UnityAds","com.flaregames.imports.unityads.UnityAdsImport")
    b.add_task("Reddit","com.flaregames.imports.reddit.RedditImport")
    b.add_task("TikTok","com.flaregames.tech.tiktok.core.AdAudienceImport")

with DagBuilder("Spectrum_Partitions","Daily update of Spectrum partitions",retry=(3,15),schedule="00:35") as b:
    #b.add_task("Spectrum_Gamesight","com.flaregames.imports.spectrum.UpdatePartitionsJob")
    b.add_task("Spectrum_PostbackAPI","com.flaregames.imports.spectrum.UpdatePostbackApiPartitions")
    b.add_task("Spectrum_PlatformGames","com.flaregames.imports.spectrum.UpdateEtlExportPartitions")
    #b.add_task("Spectrum_PopreachDB","com.flaregames.imports.spectrum.UpdatePopreachDBPartitions")

with DagBuilder("Xsolla_Store","Daily import of various stored reports",retry=(3,30),schedule="00:30") as b:
    b.add_task("Xsolla","com.flaregames.imports.xsolla.XsollaImport")

with DagBuilder("ASC_Analytics","Daily import of ASC Anylytics",retry=(4,30),schedule="05:30") as b:
    b.add_task("ASC_Reports","com.flaregames.imports.asconnect.ASCReports")

with DagBuilder("Stores","Daily import of various stored reports",retry=(4,30),schedule="03:30") as b:
    b.add_task("ASC_Financial","com.flaregames.imports.asconnect.ASCFinancialReports")
    b.add_task("ASC_Financial_Details","com.flaregames.imports.asconnect.ASCFinDetailReports")
    b.add_task("ASC_Sales","com.flaregames.imports.asconnect.ASCSalesReports")
    b.add_task("GPlay_Earnings","com.flaregames.imports.googleplay.GPlayEarningsImport")
    b.add_task("GPlay_Sales","com.flaregames.imports.googleplay.GPlaySalesImport")
    b.add_task("GPlay_Performance","com.flaregames.imports.googleplay.GPlayPerformanceImport")
    b.add_task("GPlay_Installs","com.flaregames.imports.googleplay.GPlayInstallsImport")
    b.add_task("Amazon_Sales","com.flaregames.imports.amazon.AmazonImport")
    b.add_task("Facebook_Payments","com.flaregames.imports.fb_payments.FbPaymentImport")
    b.add_task("Stripe_Transactions","com.flaregames.imports.stripe.StripeImport")
    b.add_task("Steam_MicroTxn","com.flaregames.imports.steam.SteamImport")
    b.add_task("Microsoft","com.flaregames.imports.microsoft.UwpReportImport")
    b.add_task("PayPal","com.flaregames.imports.paypal.PaypalImport")
    #b.add_task("PopreachDB_Partitions","com.flaregames.imports.spectrum.UpdatePopreachDBPartitions")
