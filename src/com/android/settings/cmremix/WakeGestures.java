/*
 * Copyright (C) 2015 AICP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cmremix;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.hardware.VibratorGestures;
import com.android.settings.cmremix.utils.FileUtils;

import java.io.File;

public class WakeGestures extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "WakeGestures";

    private static String WAKE_GESTURES_PATH = "/sys/android_touch/wake_gestures";
    private static String SWEEP2WAKE_PATH = "/sys/android_touch/sweep2wake";
    private static String SWEEP2SLEEP_PATH = "/sys/android_touch/sweep2sleep";
    private static String VIB_PATH = "/sys/android_touch/vib_strength";

    private static final String KEY_SWEEP_TO_WAKE_CATEGORY = "sweep_to_wake_category";
    private static final String KEY_SWEEP_TO_SLEEP_CATEGORY = "sweep_to_sleep_category";

    private static final String KEY_SWEEP_TO_WAKE_SWITCH = "sweep_to_wake_switch";
    private static final String KEY_SWEEP_TO_SLEEP_SWITCH = "sweep_to_sleep_switch";

    private static final String KEY_SWEEP_TO_WAKE_RIGHT = "sweep_to_wake_right";
    private static final String KEY_SWEEP_TO_WAKE_LEFT = "sweep_to_wake_left";
    private static final String KEY_SWEEP_TO_WAKE_UP = "sweep_to_wake_up";
    private static final String KEY_SWEEP_TO_WAKE_DOWN = "sweep_to_wake_down";
    private static final String KEY_SWEEP_TO_SLEEP_RIGHT = "sweep_to_sleep_right";
    private static final String KEY_SWEEP_TO_SLEEP_LEFT = "sweep_to_sleep_left";

    private static final String KEY_VIBRATION_GESTURES = "vibration_gestures";

    private static CheckBoxPreference mSweepToWakeRight;
    private static CheckBoxPreference mSweepToWakeLeft;
    private static CheckBoxPreference mSweepToWakeUp;
    private static CheckBoxPreference mSweepToWakeDown;
    private static CheckBoxPreference mSweepToSleepRight;
    private static CheckBoxPreference mSweepToSleepLeft;
    private static SwitchPreference mSweepToWakeSwitch;
    private static SwitchPreference mSweepToSleepSwitch;
    private static PreferenceCategory mSweepToWakeCategory;
    private static PreferenceCategory mSweepToSleepCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.wake_gestures);

        PreferenceScreen prefSet = getPreferenceScreen();

        mContext = getActivity().getApplicationContext();

        mSweepToWakeSwitch =
            (SwitchPreference) prefSet.findPreference(KEY_SWEEP_TO_WAKE_SWITCH);

        mSweepToSleepSwitch =
            (SwitchPreference) prefSet.findPreference(KEY_SWEEP_TO_SLEEP_SWITCH);

        mSweepToWakeRight =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_WAKE_RIGHT);
        mSweepToWakeRight.setOnPreferenceChangeListener(this);

        mSweepToWakeLeft =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_WAKE_LEFT);
        mSweepToWakeLeft.setOnPreferenceChangeListener(this);

        mSweepToWakeUp =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_WAKE_UP);
        mSweepToWakeUp.setOnPreferenceChangeListener(this);

        mSweepToWakeDown =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_WAKE_DOWN);
        mSweepToWakeDown.setOnPreferenceChangeListener(this);

        mSweepToSleepRight =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_SLEEP_RIGHT);
        mSweepToSleepRight.setOnPreferenceChangeListener(this);

        mSweepToSleepLeft =
            (CheckBoxPreference) prefSet.findPreference(KEY_SWEEP_TO_SLEEP_LEFT);
        mSweepToSleepLeft.setOnPreferenceChangeListener(this);

        mSweepToWakeCategory =
            (PreferenceCategory) prefSet.findPreference(KEY_SWEEP_TO_WAKE_CATEGORY);
        mSweepToSleepCategory =
            (PreferenceCategory) prefSet.findPreference(KEY_SWEEP_TO_SLEEP_CATEGORY);
        if (!isWakeSupported()) {
            prefSet.removePreference(mSweepToWakeCategory);
        }
        if (!isSleepSupported()) {
            prefSet.removePreference(mSweepToSleepCategory);
        }
        if (!isVibGesturesSupported()) {
            removePreference(KEY_VIBRATION_GESTURES);
        }

        writeToWakeFile();
        writeToSleepFile();

    }

    @Override
    public void onResume() {
        super.onResume();
        writeToWakeFile();
        writeToSleepFile();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (preference == mSweepToWakeRight) {
            writeToWakeFile();
        } else if (preference == mSweepToWakeLeft) {
            writeToWakeFile();
        } else if (preference == mSweepToWakeUp) {
            writeToWakeFile();
        } else if (preference == mSweepToWakeDown) {
            writeToWakeFile();
        } else if (preference == mSweepToSleepRight) {
            writeToSleepFile();
        } else if (preference == mSweepToSleepLeft) {
            writeToSleepFile();
        } else if (preference == mSweepToWakeSwitch) {
            writeToWakeFile();
            if (!mSweepToSleepSwitch.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "0");
            }
        } else if (preference == mSweepToSleepSwitch) {
            writeToSleepFile();
            if (!mSweepToSleepSwitch.isChecked()) {
                FileUtils.writeLine(SWEEP2SLEEP_PATH, "0");
            }
        }
        return true;
    }

    private void writeToWakeFile() {
        FileUtils.writeLine(SWEEP2WAKE_PATH, "0");
        FileUtils.writeLine(WAKE_GESTURES_PATH, "0");

        if (mSweepToWakeSwitch.isChecked()) {
            FileUtils.writeLine(WAKE_GESTURES_PATH, "1");
            if (mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "1");
            } else if (!mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "2");
            } else if (!mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "4");
            } else if (!mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "8");
            } else if (mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "3");
            } else if (mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "5");
            } else if (mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "9");
            } else if (!mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "6");
            } else if (mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && !mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "7");
            } else if (!mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "10");
            } else if (mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    !mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "11");
            } else if (!mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "12");
            } else if (mSweepToWakeRight.isChecked() && !mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "13");
            } else if (!mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "14");
            } else if (mSweepToWakeRight.isChecked() && mSweepToWakeLeft.isChecked() &&
                    mSweepToWakeUp.isChecked() && mSweepToWakeDown.isChecked()) {
                FileUtils.writeLine(SWEEP2WAKE_PATH, "15");
            }
        } else if (!mSweepToWakeSwitch.isChecked()) {
            FileUtils.writeLine(SWEEP2WAKE_PATH, "0");
            FileUtils.writeLine(WAKE_GESTURES_PATH, "0");
        }
    }

    private void writeToSleepFile() {
        FileUtils.writeLine(SWEEP2SLEEP_PATH, "0");
        FileUtils.writeLine(WAKE_GESTURES_PATH, "0");

        if (mSweepToSleepSwitch.isChecked()) {
            FileUtils.writeLine(WAKE_GESTURES_PATH, "1");
            if (mSweepToSleepRight.isChecked() && !mSweepToSleepLeft.isChecked()) {
                FileUtils.writeLine(SWEEP2SLEEP_PATH, "1");
            } else if (!mSweepToSleepRight.isChecked() && mSweepToSleepLeft.isChecked()) {
                FileUtils.writeLine(SWEEP2SLEEP_PATH, "2");
            } else if (mSweepToSleepRight.isChecked() && mSweepToSleepLeft.isChecked()) {
                FileUtils.writeLine(SWEEP2SLEEP_PATH, "3");
            }
        } else if (!mSweepToSleepSwitch.isChecked()) {
            FileUtils.writeLine(SWEEP2SLEEP_PATH, "0");
            FileUtils.writeLine(WAKE_GESTURES_PATH, "0");
        }
    }

    public static boolean isVibGesturesSupported() {
        return new File(VIB_PATH).exists();
    }

    public static boolean isWakeSupported() {
        return new File(SWEEP2WAKE_PATH).exists();
    }

    public static boolean isSleepSupported() {
        return new File(SWEEP2SLEEP_PATH).exists();
    }

}
