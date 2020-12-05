package com.bixin.launcher.launcherbx3.model.listener;

/**
 * @author Altair
 * @date :2020.04.01 上午 10:27
 * @description:
 */
public interface OnLocationListener {
    /**
     * location gps message
     */
    void gpsSpeedChanged();

    void updateWeather(String weatherInfo);

}
