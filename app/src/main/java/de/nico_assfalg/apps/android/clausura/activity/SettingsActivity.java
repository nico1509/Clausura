package de.nico_assfalg.apps.android.clausura.activity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private ConstraintLayout layoutPinnedText;
    private ConstraintLayout layoutPinnedDate;

    private TextView textPinnedTextValue;
    private TextView textPinnedDateValue;


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

        // About Clausura Dialog
        ConstraintLayout layoutInfo = (ConstraintLayout) findViewById(R.id.layoutInfo);
        layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
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

    private void setSwitch(Switch aSwitch, boolean checked) {
        aSwitch.setChecked(checked);
    }

    private void showInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_update);

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
