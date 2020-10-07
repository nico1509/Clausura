/*
    Copyright 2020 Nico Aßfalg

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package de.nico_assfalg.apps.android.clausura.activity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import de.nico_assfalg.apps.android.clausura.R;
import de.nico_assfalg.apps.android.clausura.helper.PreferenceHelper;
import de.nico_assfalg.apps.android.clausura.time.Date;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchShowPast;
    private Switch switchShowPinned;
    private Switch switchOnlyWifi;

    private ConstraintLayout layoutPinnedText;
    private ConstraintLayout layoutPinnedDate;
    private ConstraintLayout layoutOnlyWifi;

    private TextView textPinnedTextValue;
    private TextView textPinnedDateValue;
    private TextView textOnlyWifiDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLayoutComponents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initLayoutComponents() {
        // Show Past Setting
        ConstraintLayout layoutShowPast = (ConstraintLayout) findViewById(R.id.layoutShowPast);
        switchShowPast = (Switch) findViewById(R.id.switchShowPast);
        switchShowPast.setChecked(pastAllowed());
        layoutShowPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShowPast.setChecked(!switchShowPast.isChecked());
            }
        });
        switchShowPast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showPast(isChecked);
            }
        });

        // Show Pinned Date Setting
        ConstraintLayout layoutShowPinned = (ConstraintLayout) findViewById(R.id.layoutShowPinned);
        switchShowPinned = (Switch) findViewById(R.id.switchShowPinned);
        switchShowPinned.setChecked(pinnedAllowed());
        layoutShowPinned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShowPinned.setChecked(!switchShowPinned.isChecked());
            }
        });
        switchShowPinned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showPinned(isChecked);
                if (!isChecked) {
                    layoutPinnedText.setVisibility(View.GONE);
                    layoutPinnedDate.setVisibility(View.GONE);
                } else {
                    layoutPinnedText.setVisibility(View.VISIBLE);
                    //layoutPinnedDate.setVisibility(View.VISIBLE);
                }
            }
        });

        // Pinned Title Setting
        layoutPinnedText = (ConstraintLayout) findViewById(R.id.layoutPinnedText);
        if (!pinnedAllowed()) {
            layoutPinnedText.setVisibility(View.GONE);
        }
        textPinnedTextValue = (TextView) findViewById(R.id.textPinnedTextValue);
        textPinnedTextValue.setText("\"" + getPinnedText() + "\"");
        layoutPinnedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog();
            }
        });

        // Pinned Date Setting
        layoutPinnedDate = (ConstraintLayout) findViewById(R.id.layoutPinnedDate);
        if (!pinnedAllowed()) {
            layoutPinnedDate.setVisibility(View.GONE);
        }
        textPinnedDateValue = (TextView) findViewById(R.id.textPinnedDateValue);
        textPinnedDateValue.setText("\"" + getPinnedDate() + "\"");
        layoutPinnedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show DatePickerDialog
            }
        });

        // Update WiFi Setting
        layoutOnlyWifi = (ConstraintLayout) findViewById(R.id.layoutOnlyWifi);
        switchOnlyWifi = (Switch) findViewById(R.id.switchOnlyWifi);
        layoutOnlyWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOnlyWifi.setChecked(!switchOnlyWifi.isChecked());
            }
        });
        switchOnlyWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUpdateWifiOnly(isChecked);
                setOnlyWifiDescriptionText(isChecked);
            }
        });
        switchOnlyWifi.setChecked(updateWifiOnly());
        setOnlyWifiDescriptionText(updateWifiOnly());

        // About Clausura Dialog
        ConstraintLayout layoutInfo = (ConstraintLayout) findViewById(R.id.layoutInfo);
        layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("clausura");
            }
        });

        // About Joda-Time Dialog
        ConstraintLayout layoutInfoJoda = (ConstraintLayout) findViewById(R.id.layoutInfoJoda);
        layoutInfoJoda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog("joda");
            }
        });

    }

    private void showPast(boolean enabled) {
        if (!enabled) {
            PreferenceHelper.setPreference(getApplicationContext(), "0", "showPast");
        } else {
            PreferenceHelper.setPreference(getApplicationContext(), "1", "showPast");
        }
    }

    private boolean pastAllowed() {
        String pastSetting = PreferenceHelper.getPreference(getApplicationContext(), "showPast");
        if (pastSetting.equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    private void showPinned(boolean enabled) {
        if (!enabled) {
            PreferenceHelper.setPreference(getApplicationContext(), "0", "showLectureEnd");
        } else {
            PreferenceHelper.setPreference(getApplicationContext(), "1", "showLectureEnd");
        }
    }

    private boolean pinnedAllowed() {
        String pinnedSetting = PreferenceHelper.getPreference(getApplicationContext(), "showLectureEnd");
        if (pinnedSetting.equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean updateWifiOnly() {
        String updateWifiOnly = PreferenceHelper.getPreference(this, PreferenceHelper.UPDATE_WIFI_ONLY);
        if (updateWifiOnly.equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    private void setUpdateWifiOnly(boolean wifiOnly) {
        if (wifiOnly) {
            PreferenceHelper.setPreference(this, "1", PreferenceHelper.UPDATE_WIFI_ONLY);
        } else {
            PreferenceHelper.setPreference(this, "0", PreferenceHelper.UPDATE_WIFI_ONLY);
        }
    }

    private void setOnlyWifiDescriptionText(boolean isChecked) {
        textOnlyWifiDescription = (TextView) findViewById(R.id.textOnlyWifiDescription);
        if (!isChecked) {
            textOnlyWifiDescription.setText(getString(R.string.text_only_wifi_explanation_false));
        } else {
            textOnlyWifiDescription.setText(getString(R.string.text_only_wifi_explanation));
        }
    }

    private String getPinnedText() {
        return PreferenceHelper.getPreference(getApplicationContext(),
                PreferenceHelper.PINNED_DATE_TITLE);
    }

    private void setPinnedText(String text) {
        PreferenceHelper.setPreference(getApplicationContext(), text, PreferenceHelper.PINNED_DATE_TITLE);
        textPinnedTextValue.setText(text);
    }

    private String getPinnedDate() {
        Date date = new Date(PreferenceHelper.getPreference(getApplicationContext(), "endOfLectureDate"));
        return date.toHumanString();
    }

    private void showInfoDialog(String product) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_update);

        TextView infoHeadline = (TextView) dialog.findViewById(R.id.infoHeadline);
        TextView textLicense = (TextView) dialog.findViewById(R.id.textLicense);
        TextView textAdditionalInfo = (TextView) dialog.findViewById(R.id.textAdditionalInfo);

        Button closeButton = (Button) dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button updateButton = (Button) dialog.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://nico-assfalg.de/Clausura/updateChecker.php?thisversion="
                        + PreferenceHelper.getAppVersion(getApplicationContext());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    builder.setToolbarColor(getColor(R.color.colorPrimary));
                }
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(SettingsActivity.this, Uri.parse(url));
                dialog.dismiss();
            }
        });

        switch (product) {
            case "clausura":
                infoHeadline.setText(getString(R.string.app_name) + " " + PreferenceHelper.getAppVersion(this));
                textLicense.setText(getString(R.string.text_license));
                textAdditionalInfo.setText(getString(R.string.text_logo_credit));
                break;
            case "joda":
                infoHeadline.setText(getString(R.string.text_joda_version));
                textLicense.setText(getString(R.string.text_license_joda));
                textAdditionalInfo.setText(getString(R.string.text_joda_link));
                break;
        }

        //Set Width to match_parent
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    private void showEditTitleDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_pinned_edit);

        final EditText editTitle = (EditText) dialog.findViewById(R.id.editTitle);
        editTitle.setText(getPinnedText());

        Button buttonSave = (Button) dialog.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().equals("")
                        ? editTitle.getHint().toString() : editTitle.getText().toString();
                setPinnedText(title);
                dialog.dismiss();
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Set Width to match_parent
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

}
