package com.amra.calendar;

import static com.amra.calendar.service.DateConverterService.FORMATTER;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.amra.calendar.service.DateConverterService;
import com.amra.calendar.validator.TextValidator;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class MainActivity extends AppCompatActivity {

    private final DateConverterService service = new DateConverterService();
    // on below line we are creating variables.
    private EditText selectedDate;
    private EditText tutLabel;
    private EditText heliadaDate;
    private NumberPicker numberPicker;
    private TextView heliadasInCommon;
    private TextView heliadasInTUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSwitchButton();

        LocalDate today = LocalDate.now();
        tutLabel = findViewById(R.id.idTUTLabel);
        tutLabel.setText(service.convertToTUT(today.format(FORMATTER)));

        selectedDate = findViewById(R.id.idTVSelectedDate);
        selectedDate.setText(today.format(FORMATTER));
        selectedDate.setOnClickListener(v -> {
            LocalDate selectedDate = LocalDate.parse(this.selectedDate.getText(), FORMATTER);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        this.selectedDate.setText(date.format(FORMATTER));
                        String converted = service.convertToTUT(date);
                        tutLabel.setText(converted);
                    },
                    selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());
            datePickerDialog.show();
        });

        tutLabel.addTextChangedListener(new TextValidator(tutLabel) {
            @Override
            public void validate(TextView textView, String text) {
                String tutInput = text.trim();
                if (tutInput.endsWith(".")) {
                    tutLabel.setError("Incorrect format");
                }
                Pattern pattern = Pattern.compile(DateConverterService.TUT_FORMAT_REGEX);
                Matcher matcher = pattern.matcher(tutInput);
                if (matcher.groupCount() != 4) {
                    tutLabel.setError("Incorrect format");
                } else {
                    String heliadaDigit = matcher.group(1);
                    String gekatontadaDigit = matcher.group(2);
                    String decadaDigit = matcher.group(3);
                    String dayOfDecadaDigit = matcher.group(4);
                    if (TextUtils.isEmpty(heliadaDigit)
                            || TextUtils.isEmpty(gekatontadaDigit)
                            || TextUtils.isEmpty(decadaDigit)
                            || TextUtils.isEmpty(dayOfDecadaDigit)) {
                        tutLabel.setError("Incorrect format");
                    }
                }
            }
        });


        heliadasInCommon = findViewById(R.id.heliadasInCommon);
        heliadasInTUT = findViewById(R.id.heliadasInTUT);
        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);
        numberPicker.setValue(1);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            LocalDate heliadaDate = LocalDate.parse(this.heliadaDate.getText(), FORMATTER);
            calculateHeliadas(heliadaDate);
        });
        calculateHeliadas(today);

        heliadaDate = findViewById(R.id.heliadaDate);
        heliadaDate.setText(today.format(FORMATTER));
        heliadaDate.setOnClickListener(v -> {
            LocalDate heliadaDate = LocalDate.parse(this.heliadaDate.getText(), FORMATTER);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        this.heliadaDate.setText(date.format(FORMATTER));
                        calculateHeliadas(date);
                    },
                    heliadaDate.getYear(), heliadaDate.getMonthValue() - 1, heliadaDate.getDayOfMonth());
            datePickerDialog.show();
        });

    }

    private void calculateHeliadas(LocalDate date) {
        LocalDate resultDate = date.plusDays(1000L * numberPicker.getValue());
        heliadasInCommon.setText(resultDate.format(FORMATTER));
        heliadasInTUT.setText(service.convertToTUT(resultDate));
    }

    private void initSwitchButton() {
        ImageButton btnSwitch = findViewById(R.id.idBtnSwitch);
        Drawable switch_png = ResourcesCompat.getDrawable(getResources(), R.drawable.switch_calendar, getTheme());
        btnSwitch.setImageDrawable(switch_png);
    }
}