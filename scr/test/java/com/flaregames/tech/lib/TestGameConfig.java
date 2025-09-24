package com.flaregames.tech.lib;

import com.flaregames.tech.fbinsights.DateUtil;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGameConfig {

    @BeforeClass
    public static void assertUTC() {
        DateUtil.AssertUTC();
    }

    private void checkUnique(List<GameConfig> list, Function<GameConfig, ? extends Object> func) {
        List<GameConfig> clean = list.stream().filter(gc -> func.apply(gc) != null).collect(Collectors.toList());
        //clean.stream().map(func).sorted().forEach(System.out::println);
        Assert.assertEquals(
                clean.stream().map(func).collect(Collectors.toSet()).size(),
                clean.stream().map(func).count());
    }

    @Test
    public void testIntegrity() {
        List<GameConfig> list = GameConfig.getConfigs();
        checkUnique(list, GameConfig::getGameId);
        checkUnique(list, GameConfig::getShortId);
        checkUnique(list, GameConfig::getFacebookAppId);
        checkUnique(list, GameConfig::getStoreLinkIOS);
        checkUnique(list, GameConfig::getStoreLinkAndroid);
        checkUnique(list, GameConfig::getAppsflyerAppId);
        checkUnique(list, GameConfig::getAppleAppSku);
    }

    @Test
    public void testAFPackageName() {

        Optional<GameConfig> nsk2 = GameConfig.getConfigForALPackageName("com.koplagames.kopla02");
        assertThat(nsk2).isPresent();

        Optional<GameConfig> bogus = GameConfig.getConfigForALPackageName("com.test.bogus");
        assertThat(bogus).isNotPresent();

    }

}
