/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mozilla.focus.activity.TestHelper.waitingTime;


@RunWith(AndroidJUnit4.class)
public class URLCompletionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class) {

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
        }
    };

    @After
    public void tearDown() throws Exception {
        mActivityTestRule.getActivity().finishAndRemoveTask();
    }

    @Test
    public void CompletionTest() throws InterruptedException, UiObjectNotFoundException {
        /* type a partial url, and check it autocompletes*/
        TestHelper.inlineAutocompleteEditText.waitForExists(waitingTime);
        TestHelper.inlineAutocompleteEditText.setText("mozilla");
        TestHelper.hint.waitForExists(waitingTime);
        assertTrue (TestHelper.inlineAutocompleteEditText.getText().equals("mozilla.org"));

        /* press x to delete the both autocomplete and suggestion */
        TestHelper.cleartextField.click();
        assertTrue (TestHelper.inlineAutocompleteEditText.getText().equals("Search or enter address"));
        assertFalse (TestHelper.hint.exists());

        /* type a full url, and check it does not autocomplete */
        TestHelper.inlineAutocompleteEditText.setText("http://www.mozilla.org");
        TestHelper.hint.waitForExists(waitingTime);
        assertTrue (TestHelper.inlineAutocompleteEditText.getText().equals("http://www.mozilla.org"));
    }
}
