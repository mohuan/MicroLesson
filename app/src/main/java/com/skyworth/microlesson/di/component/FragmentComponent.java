package com.skyworth.microlesson.di.component;

import android.app.Activity;


import com.skyworth.microlesson.di.module.FragmentModule;
import com.skyworth.microlesson.di.scope.FragmentScope;
import com.skyworth.microlesson.ui.main.fragment.DoodleFragment;

import dagger.Component;

/**
 * Created by skyworth on 16/8/7.
 */

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

    void inject(DoodleFragment doodleFragment);

//    void inject(HomeFragment homeFragment);

//    void inject(HistoryFragment historyFragment);

}
