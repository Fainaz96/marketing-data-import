
# Marketing Data Import

## Airflow

airlfow is installed here: 3.218.196.205

The airflow User interface can be accessed in the browser (http://localhost:8080) with an ssh forwarding to e.g. 8080: 

`ssh -L 8080:localhost:8080 python@3.218.196.205`

## Facebook Daily Insights

This job has 2 executions:

```
fb_daily_insights: will import the last 3 days
fb_history_insights: will import the last 28 days and also write data in the history table
```
### How to start

```
java -classpath /<path to>/marketing-data-import-0.1.jar -Duser.timezone=UTC com.flaregames.tech.fbinsights.FBDailyInsights airflow
java -classpath /<path to>/marketing-data-import-0.1.jar -Duser.timezone=UTC com.flaregames.tech.fbinsights.FBHistoryInsights airflow
```

### How to configure

The import reads the ad accounts that should be imported from the table `partners.import_conf`. More ad accounts can be added by inserting new rows there:

```
INSERT INTO partners.import_conf VALUES('fb_daily_insights',NULL,'adaccounts','<new add account id>',NULL,true,'<some info you want to provide>');
```

### Import Log
for every successfull import of an adaccount you can find one entry in the table `partners.import_log` for the complete import there is an entry named 'ALL'

```
SELECT * FROM partners.import_log WHERE job_name = 'fb_daily_insights' AND log_stamp = CURRENT_DATE;
SELECT * FROM partners.import_log WHERE job_name = 'fb_history_insights' AND log_stamp = CURRENT_DATE;
``` 

### Resulting data

Data is loaded with 28 day click and view attribution window with country breakdown.

- `partners.fb_ad_data` contains the imported insights per day
- `partners.fb_ad_data_hist` contains the imported insights per day historically (28 days back for every import execution day)

### Airflow

TECH_Facebook_Daily_Insights task is configured inside the DAG: MarketingPipelineOptimized_v2

## Appsflyer

### How to start

```
java -classpath /<path to>/marketing-data-import-0.1.jar -Duser.timezone=UTC com.flaregames.tech.appsflyer.AFDataLockerLoader airflow
```

The import reads "clicks", "impressions", "installs", "inapp" from Appsflyer Data Locker V2

The resulting structure is:

- `partners.af_data_clicks`
- `partners.af_data_installs`
- `partners.af_data_inapp`
- `partners.af_data_impressions`

(the import creates monthly sliced tables and the corresponding views)


### Import Log

for every successfull import of a report you can find one entry in the table `partners.import_log`
This import is done hourly and is logged in the import_log table using the log_hour column.
Appsflyer has a "late" hour for events within a specific time range (around 0h), this late hour is logged has log_hour = 24   

```
SELECT * FROM partners.import_log WHERE job_name = 'af_daily_import' AND log_stamp = CURRENT_DATE;
``` 

### Airflow

TECH_Appsflyer_Daily_Import is configured as a separate DAG
(might will also be moved in the MarketingPipelineOptimized_v2)

## Applovin

### How to start

```
java -classpath /<path to>/marketing-data-import-0.1.jar -Duser.timezone=UTC com.flaregames.tech.applovin.ApplovinLoader airflow
```

The import reads the advertiser data from Applovin
(https://growth-support.applovin.com/hc/en-us/articles/115000784688-Basic-Reporting-API)

The resulting structure is (timesliced with a view):

- `partners.applovin_costs`

The import requires that applovinPackageNames is set to an array of packagenames for the games

### Import Log
for every successfull import of a report you can find one entry in the table `partners.import_log` for the complete import there is an entry named 'ALL'

```
SELECT * FROM partners.import_log WHERE job_name = 'applovin_costs' AND log_stamp = CURRENT_DATE;
``` 

### Airflow

TECH_Appslovin_Costs_Import is configured as a separate DAG


## GameConfig

in `resources/game_config.json` all games are configured. 
- facebookAppId is used for resolving the the gameId of facebook ads
- appsflyerAppId is used for resolving the the gameId of appsflyer events
- applovinPackageNames is used for resolving the the gameId of applovin cost data

## How to build
Maven:
```
mvn clean package
```
creates uber-jar target/marketing-data-import-0.1.jar

```
build.sh [deploy]
```
builds the package and removes signatures (required due to uber-jar)
optionally 'deploy' puts the package where it belongs on 3.218.196.205


