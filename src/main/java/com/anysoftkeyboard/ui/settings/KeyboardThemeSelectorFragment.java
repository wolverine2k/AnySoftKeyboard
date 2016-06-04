/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anysoftkeyboard.ui.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.preference.PreferenceFragment;
import android.view.View;

import com.anysoftkeyboard.keyboards.AnyKeyboard;
import com.anysoftkeyboard.keyboards.KeyboardFactory;
import com.anysoftkeyboard.keyboards.views.DemoAnyKeyboardView;
import com.anysoftkeyboard.theme.KeyboardTheme;
import com.anysoftkeyboard.theme.KeyboardThemeFactory;
import com.menny.android.anysoftkeyboard.R;

import net.evendanan.chauffeur.lib.FragmentChauffeurActivity;
import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

import java.util.Collections;
import java.util.List;

public class KeyboardThemeSelectorFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.prefs_addon_keyboard_theme_selector);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference(getString(R.string.tweaks_group_key)).setOnPreferenceClickListener(this);
        final Preference openBrowserItem = findPreference(getString(R.string.settings_key_keyboard_theme_key));
        openBrowserItem.setOnPreferenceClickListener(this);
        openBrowserItem.setSummary(getString(R.string.keyboard_theme_summary, KeyboardThemeFactory.getCurrentKeyboardTheme(getContext()).getName()));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof FragmentChauffeurActivity) {
            FragmentChauffeurActivity chauffeurActivity = (FragmentChauffeurActivity) activity;
            if (preference.getKey().equals(getString(R.string.tweaks_group_key))) {
                chauffeurActivity.addFragmentToUi(new KeyboardThemeTweaksFragment(), TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
                return true;
            } else if (preference.getKey().equals(getString(R.string.settings_key_keyboard_theme_key))) {
                chauffeurActivity.addFragmentToUi(new KeyboardAddOnBrowserFragment(), TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
                return true;
            }
        }
        return false;
    }

    public static class KeyboardAddOnBrowserFragment extends AbstractKeyboardAddOnsBrowserFragment<KeyboardTheme> {

        @NonNull
        @Override
        protected String getFragmentTag() {
            return "KeyboardAddOnBrowserFragment";
        }

        @StringRes
        @Override
        protected int getFragmentTitleResourceId() {
            return R.string.keyboard_theme_list_title;
        }

        @NonNull
        @Override
        protected List<KeyboardTheme> getEnabledAddOns() {
            return Collections.singletonList(KeyboardThemeFactory.getCurrentKeyboardTheme(getContext()));
        }

        @NonNull
        @Override
        protected List<KeyboardTheme> getAllAvailableAddOns() {
            return KeyboardThemeFactory.getAllAvailableThemes(getContext());
        }

        @Override
        protected void onEnabledAddOnsChanged(@NonNull List<String> newEnabledAddOns) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putString(getString(R.string.settings_key_keyboard_theme_key), newEnabledAddOns.get(0));
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }

        @Override
        protected boolean isSingleSelectedAddOn() {
            return true;
        }

        @Override
        protected void applyAddOnToDemoKeyboardView(@NonNull KeyboardTheme addOn, @NonNull DemoAnyKeyboardView demoKeyboardView) {
            demoKeyboardView.resetKeyboardTheme(addOn);
            AnyKeyboard defaultKeyboard = KeyboardFactory.getEnabledKeyboards(getContext()).get(0).createKeyboard(getContext(), getResources().getInteger(R.integer.keyboard_mode_normal));
            defaultKeyboard.loadKeyboard(demoKeyboardView.getThemedKeyboardDimens());
            demoKeyboardView.setKeyboard(defaultKeyboard);
        }
    }
}