package com.chingiz.ecg;

import androidx.appcompat.app.AppCompatActivity;
import br.unb.biologiaanimal.edf.EDF;
import ru.mipt.edf.EDFParser;
import ru.mipt.edf.EDFParserResult;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.extensions.builders.SciChartBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {


    Spinner spinner;
    SciChartSurface surface;
    double[] signal;
    XyDataSeries lineData;
    EDF edf;
    String labels[];
    IRenderableSeries lineSeries;
    EDFParserResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner) findViewById(R.id.spinner);
        String license = "<LicenseContract>" +
                "  <Customer>mrchingiz98@gmail.com</Customer>" +
                "  <OrderId>Trial</OrderId>" +
                "  <LicenseCount>1</LicenseCount>" +
                "  <IsTrialLicense>true</IsTrialLicense>" +
                "  <SupportExpires>12/21/2019 00:00:00</SupportExpires>" +
                "  <ProductCode>SC-ANDROID-2D-ENTERPRISE-SRC</ProductCode>" +
                "  <KeyCode>9abab4a03284039af57398850586c6c7ea3101d4bdbc6dcf0e0434c6836e560cd5b84c34b4b59511f2744f648929d2cc9773e5e3b31d07394dd68a3b67f9fa7c03d12f255e593434cda3d078277c89c35f56f8f78d90f1e3c45e3d60d47eacd8a62cc7c5230f0fcae4943d088b061e5cf7ff59189df8ba883bea1faacec72fe11091b7f83d50fc42235b350bb9298f4944b3ee435c345c0d25ad0fa0db199c113f8de990fd515802e1944df1ec6a53</KeyCode>" +
                "</LicenseContract>";
        try {
            SciChartSurface.setRuntimeLicenseKey(license);
        } catch (Exception e){

        }

        InputStream is = null;
        result = null;
        try {
            is = new BufferedInputStream(new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ecgca102.edf")));
            result = EDFParser.parseEDF(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        labels = result.getHeader().getChannelLabels();
        signal = result.getSignal().getValuesInUnits()[0];
        ArrayList<String> arrayList = new ArrayList<>();

        for(String i : labels){
            arrayList.add(i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        surface = new SciChartSurface(this);

        LinearLayout chartLayout = (LinearLayout) findViewById(R.id.chart_layout);
        chartLayout.addView(surface);
        SciChartBuilder.init(this);
        final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();

        final IAxis xAxis = sciChartBuilder.newNumericAxis()
                .withAxisTitle("")
                .withVisibleRange(0, 1000)
                .build();

        double min = signal[0];
        double max = signal[0];
        for(int i = 1; i < signal.length; i++){
            if(signal[i] < min)
                min = signal[i];
            if(signal[i] > max)
                max = signal[i];
        }

        final IAxis yAxis = sciChartBuilder.newNumericAxis()
                .withAxisTitle("").withVisibleRange(min, max).build();

        ModifierGroup chartModifiers = sciChartBuilder.newModifierGroup()
                .withPinchZoomModifier().withReceiveHandledEvents(true).build()
                .withZoomPanModifier().withReceiveHandledEvents(true).build()
                .build();

        Collections.addAll(surface.getYAxes(), yAxis);
        Collections.addAll(surface.getXAxes(), xAxis);
        Collections.addAll(surface.getChartModifiers(), chartModifiers);
        lineData = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).build();
        for (int i = 0; i < signal.length; i++)
        {
            lineData.append(i, Double.valueOf(signal[i]));
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tutorialsName = parent.getItemAtPosition(position).toString();

                surface.getYAxes().clear();
                surface.getXAxes().clear();
                surface.getChartModifiers().clear();
                surface.getRenderableSeries().clear();

                signal = result.getSignal().getValuesInUnits()[position];

                final IAxis xAxis = sciChartBuilder.newNumericAxis()
                        .withAxisTitle("")
                        .withVisibleRange(0, 1000)
                        .build();

                double min = signal[0];
                double max = signal[0];
                for(int i = 1; i < signal.length; i++){
                    if(signal[i] < min)
                        min = signal[i];
                    if(signal[i] > max)
                        max = signal[i];
                }

                final IAxis yAxis = sciChartBuilder.newNumericAxis()
                        .withAxisTitle("").withVisibleRange(min, max).build();

                ModifierGroup chartModifiers = sciChartBuilder.newModifierGroup()
                        .withPinchZoomModifier().withReceiveHandledEvents(true).build()
                        .withZoomPanModifier().withReceiveHandledEvents(true).build()
                        .build();

                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getChartModifiers(), chartModifiers);


                Toast.makeText(parent.getContext(), "Selected: " +  signal[0],Toast.LENGTH_LONG).show();
                Log.d("SAS", "" + signal[0]);
                lineData = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).build();
                lineSeries = sciChartBuilder.newLineSeries()
                        .withDataSeries(lineData)
                        .withStrokeStyle(ColorUtil.LightBlue, 2f, true)
                        .build();
                for (int i = 0; i < signal.length; i++)
                {
                    lineData.append(i, Double.valueOf(signal[i]));
                }
                surface.getRenderableSeries().add(lineSeries);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });




    }
}
