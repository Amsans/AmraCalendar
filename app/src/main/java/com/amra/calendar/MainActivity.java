package com.amra.calendar;

import static com.amra.calendar.service.DateConverterService.FORMATTER;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.amra.calendar.service.DateConverterService;
import com.amra.calendar.validator.TextValidator;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final DateConverterService service = new DateConverterService();

    private EditText commonDateInput;
    private EditText tutDateInput;
    private EditText chiliadDate;
    private NumberPicker chiliadUpDown;
    private TextView chiliadInCommon;
    private TextView chiliadInTUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView btnSwitch = findViewById(R.id.idBtnSwitch);
        Drawable flower_ico = ResourcesCompat.getDrawable(getResources(), R.drawable.fire_flower_ico, getTheme());
        btnSwitch.setImageDrawable(flower_ico);

        LocalDate today = LocalDate.now();
        commonDateInput = findViewById(R.id.commonDateInput);
        commonDateInput.setText(today.format(FORMATTER));
        commonDateInput.setOnClickListener(v -> {
            LocalDate selectedDate = LocalDate.parse(this.commonDateInput.getText(), FORMATTER);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        this.commonDateInput.setText(date.format(FORMATTER));
                        String converted = service.convertToTUT(date);
                        tutDateInput.setText(converted);
                    },
                    selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());
            datePickerDialog.show();
        });

        tutDateInput = findViewById(R.id.tutDateInput);
        tutDateInput.setText(service.convertToTUT(today.format(FORMATTER)));
        tutDateInput.addTextChangedListener(new TextValidator(tutDateInput) {
            @Override
            public void validate(TextView textView, String text) {
                String tutInput = text.trim();
                if (tutInput.endsWith(".")) {
                    tutDateInput.setError("Incorrect format");
                }
                Pattern pattern = Pattern.compile(DateConverterService.TUT_FORMAT_REGEX);
                Matcher matcher = pattern.matcher(tutInput);
                if (!matcher.find()) {
                    tutDateInput.setError("Incorrect format");
                } else {
                    String heliadaDigit = matcher.group(1);
                    String gekatontadaDigit = matcher.group(2);
                    String decadaDigit = matcher.group(3);
                    String dayOfDecadaDigit = matcher.group(4);
                    if (TextUtils.isEmpty(heliadaDigit)
                            || TextUtils.isEmpty(gekatontadaDigit)
                            || TextUtils.isEmpty(decadaDigit)
                            || TextUtils.isEmpty(dayOfDecadaDigit)) {
                        tutDateInput.setError("Incorrect format");
                    }
                    if (tutDateInput.getError() == null) {
                        String commonConverted = service.convertToCommon(tutInput);
                        commonDateInput.setText(commonConverted);
                    }
                }
            }
        });


        chiliadInCommon = findViewById(R.id.chiliadsInCommon);
        chiliadInTUT = findViewById(R.id.chiliadsInTUT);
        chiliadUpDown = findViewById(R.id.chiliadUpDown);
        chiliadUpDown.setMinValue(1);
        chiliadUpDown.setMaxValue(999);
        chiliadUpDown.setValue(1);
        chiliadUpDown.setOnValueChangedListener((picker, oldVal, newVal) -> {
            LocalDate chiliadDate = LocalDate.parse(this.chiliadDate.getText(), FORMATTER);
            calculateChiliads(chiliadDate);
        });
        calculateChiliads(today);

        chiliadDate = findViewById(R.id.chiliadDate);
        chiliadDate.setText(today.format(FORMATTER));
        chiliadDate.setOnClickListener(v -> {
            LocalDate chiliadDate = LocalDate.parse(this.chiliadDate.getText(), FORMATTER);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        this.chiliadDate.setText(date.format(FORMATTER));
                        calculateChiliads(date);
                    },
                    chiliadDate.getYear(), chiliadDate.getMonthValue() - 1, chiliadDate.getDayOfMonth());
            datePickerDialog.show();
        });

    }

    private void calculateChiliads(LocalDate date) {
        LocalDate resultDate = date.plusDays(1000L * chiliadUpDown.getValue());
        chiliadInCommon.setText(resultDate.format(FORMATTER));
        chiliadInTUT.setText(service.convertToTUT(resultDate));
    }
}