package com.example.zlc.finallyclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import java.util.Calendar;;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Date;

/**
 * Created by ZLC on 2016/8/22.
 */
public class AlarmView extends LinearLayout {
    public AlarmView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlarmView(Context context) {
        super(context);
        init();
    }

    private void init(){
        alarmManager =(AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    protected void onFinishInflate(){
        super.onFinishInflate();

        btnAddAlarm = (ImageButton)findViewById(R.id.btnAddAlarm);
        lvAlarmList = (ListView)findViewById(R.id.lvAlarmList);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);
        lvAlarmList.setAdapter(adapter);

        readSavedAlarmList();

        btnAddAlarm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                addAlarm();
            }
        });


        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(new CharSequence[]{"删除"},new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                        switch (which){
                            case 0:
                                delateAlarm(position);
                                break;
                            default:
                                break;
                        }
                    }
                }).setNegativeButton("取消",null).show();
                return true;
            }
        });
    }

    private void delateAlarm(int position){
        AlarmDate ad = adapter.getItem(position);
        adapter.remove(ad);
        savaAlarmList();

        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));;
    }

    private void addAlarm(){
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hourofDay);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                Calendar currentTime = Calendar.getInstance();

                if(calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                }

                AlarmDate ad = new AlarmDate(calendar.getTimeInMillis());

                adapter.add(ad);
                alarmManager.set(AlarmManager.RTC_WAKEUP,ad.getTime(),PendingIntent.getBroadcast(getContext(),ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),5*60*1000, PendingIntent.getBroadcast(getContext(),ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));
                savaAlarmList();
            }

        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
    }

    private void savaAlarmList(){
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmView.class.getName(),Context.MODE_PRIVATE).edit();

        StringBuffer sb = new StringBuffer();
        for (int i = 0;i < adapter.getCount(); i++){
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        if(sb.length() > 1){
            String content = sb.toString().substring(0,sb.length()-1);
            editor.putString(KEY_ALARM_LIST,content);
            System.out.println(content);
        }else {
            editor.putString(KEY_ALARM_LIST,null);
        }

        editor.commit();
    }

    private void readSavedAlarmList(){
        SharedPreferences sp = getContext().getSharedPreferences(AlarmView.class.getName(),Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST,null);

        if(content!=null){
            String[] timeString = content.split(",");
            for(String string : timeString){
                adapter.add(new AlarmDate(Long.parseLong(string)));
            }
        }
    }

    private ImageButton btnAddAlarm;
    private ListView lvAlarmList;
    private static final String KEY_ALARM_LIST = "alarmList";
    private ArrayAdapter<AlarmDate> adapter;
    private AlarmManager alarmManager;

    private static class AlarmDate{

        private long time = 0;
        private String timeLabel = "";
        private Calendar data;

        public AlarmDate(long time){
            this.time = time;

            data = Calendar.getInstance();
            data.setTimeInMillis(time);

            timeLabel = String.format("%d月%d日 %d:%d",
                    data.get(Calendar.MONTH)+1,
                    data.get(Calendar.DAY_OF_MONTH),
                    data.get(Calendar.HOUR_OF_DAY),
                    data.get(Calendar.MINUTE));
        }

        public long getTime(){
            return time;
        }

        public String getTimeLabel(){
            return timeLabel;
        }

        @Override
        public String toString(){
            return getTimeLabel();
        }

        public int getId(){
            return (int)(getTime()/1000/60);
        }

    }



}