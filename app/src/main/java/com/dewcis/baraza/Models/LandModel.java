package com.dewcis.baraza.Models;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Faith on 4/20/2018.
 */

public class LandModel {

    String plotNumber, plotLocation, plotSize;

    public String getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(String plotNumber) {
        this.plotNumber = plotNumber;
    }

    public String getPlotLocation() {
        return plotLocation;
    }

    public void setPlotLocation(String plotLocation) {
        this.plotLocation = plotLocation;
    }

    public String getPlotSize() {
        return plotSize;
    }

    public void setPlotSize(String plotSize) {
        this.plotSize = plotSize;
    }
}
