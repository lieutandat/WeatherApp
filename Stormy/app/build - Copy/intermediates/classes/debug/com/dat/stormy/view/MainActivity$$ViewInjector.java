// Generated code from Butter Knife. Do not modify!
package com.dat.stormy.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MainActivity$$ViewInjector<T extends com.dat.stormy.view.MainActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296340, "field 'mTimeLabel'");
    target.mTimeLabel = finder.castView(view, 2131296340, "field 'mTimeLabel'");
    view = finder.findRequiredView(source, 2131296341, "field 'mTemperatureValue'");
    target.mTemperatureValue = finder.castView(view, 2131296341, "field 'mTemperatureValue'");
    view = finder.findRequiredView(source, 2131296347, "field 'mHumidityLabel'");
    target.mHumidityLabel = finder.castView(view, 2131296347, "field 'mHumidityLabel'");
    view = finder.findRequiredView(source, 2131296349, "field 'mPercipValue'");
    target.mPercipValue = finder.castView(view, 2131296349, "field 'mPercipValue'");
    view = finder.findRequiredView(source, 2131296350, "field 'mSummaryLabel'");
    target.mSummaryLabel = finder.castView(view, 2131296350, "field 'mSummaryLabel'");
    view = finder.findRequiredView(source, 2131296344, "field 'mIconImageView'");
    target.mIconImageView = finder.castView(view, 2131296344, "field 'mIconImageView'");
    view = finder.findRequiredView(source, 2131296339, "field 'mLocationlabel'");
    target.mLocationlabel = finder.castView(view, 2131296339, "field 'mLocationlabel'");
    view = finder.findRequiredView(source, 2131296351, "field 'mChart'");
    target.mChart = finder.castView(view, 2131296351, "field 'mChart'");
    view = finder.findRequiredView(source, 2131296338, "field 'mLinearLayout'");
    target.mLinearLayout = finder.castView(view, 2131296338, "field 'mLinearLayout'");
  }

  @Override public void reset(T target) {
    target.mTimeLabel = null;
    target.mTemperatureValue = null;
    target.mHumidityLabel = null;
    target.mPercipValue = null;
    target.mSummaryLabel = null;
    target.mIconImageView = null;
    target.mLocationlabel = null;
    target.mChart = null;
    target.mLinearLayout = null;
  }
}
