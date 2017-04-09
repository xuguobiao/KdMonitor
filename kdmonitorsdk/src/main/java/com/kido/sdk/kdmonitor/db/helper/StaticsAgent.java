package com.kido.sdk.kdmonitor.db.helper;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.kido.sdk.kdmonitor.db.KdNote;
import com.kido.sdk.kdmonitor.db.database.DataAccess;
import com.kido.sdk.kdmonitor.db.database.ReadDataBaseAccess;
import com.kido.sdk.kdmonitor.db.database.WriteDataBaseAccess;
import com.kido.sdk.kdmonitor.model.AppAction;
import com.kido.sdk.kdmonitor.model.DataBlock;
import com.kido.sdk.kdmonitor.model.Event;
import com.kido.sdk.kdmonitor.model.ExceptionInfo;
import com.kido.sdk.kdmonitor.model.Page;
import com.kido.sdk.kdmonitor.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class StaticsAgent {
    private static Context mContext;

    public static void init(Context context) {

        mContext = context;

        DataAccess.shareInstance(context).createAllTables();

    }

    public static void storeAppAction(String appAction) {
        if (TextUtils.isEmpty(appAction))
            throw new NullPointerException("appAction is null");
        storeData(appAction, "", "");
    }

    public static void storePage(String pageString) {
        if (TextUtils.isEmpty(pageString))
            throw new NullPointerException("pageString is null");
        storeData("", pageString, "");
    }

    public static void storeEvent(String eventString) {
        if (TextUtils.isEmpty(eventString))
            throw new NullPointerException("eventString is null");
        storeData("", "", eventString);
    }

    public static void storeException(String exceptionInfo) {
        if (TextUtils.isEmpty(exceptionInfo))
            throw new NullPointerException("exceptionInfo is null");
        storeData("", "", "", exceptionInfo);
    }

    public static DataBlock getDataBlock() {
        DataBlock dataBlock = new DataBlock();
        List<KdNote> list = ReadDataBaseAccess.shareInstance(mContext).loadAll();
        AppAction appAction = new AppAction();
        Page page = new Page();
        Event event = new Event();
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        List<AppAction> actionList = new ArrayList<>();
        List<Page> pageList = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();
        List<ExceptionInfo> exceptionInfos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(list.get(i).getFirstCloumn())) {
                appAction = JsonUtil.parseObject(list.get(i).getFirstCloumn(), AppAction.class);
                actionList.add(appAction);
            }
            if (!TextUtils.isEmpty(list.get(i).getSecondCloumn())) {
                page = JsonUtil.parseObject(list.get(i).getSecondCloumn(), Page.class);
                pageList.add(page);
            }
            if (!TextUtils.isEmpty(list.get(i).getThirdCloumn())) {
                event = JsonUtil.parseObject(list.get(i).getThirdCloumn(), Event.class);
                eventList.add(event);
            }
            if (!TextUtils.isEmpty(list.get(i).getForthCloumn())) {
                exceptionInfo = JsonUtil.parseObject(list.get(i).getForthCloumn(), ExceptionInfo.class);
                exceptionInfos.add(exceptionInfo);
            }
        }
        dataBlock.setApp_action(actionList);
        dataBlock.setPage(pageList);
        dataBlock.setExceptionInfos(exceptionInfos);
        dataBlock.setEvent(eventList);
        return dataBlock;
    }

    public static void storeData(String firstcloumn, String secondcloumn, String thirdcloumn) {
        storeData(firstcloumn, secondcloumn, thirdcloumn, null);
    }


    public static void storeData(String firstcloumn, String secondcloumn, String thirdcloumn, String forthCloumn) {
        KdNote note = new KdNote(null, firstcloumn, secondcloumn, thirdcloumn, forthCloumn);
        WriteDataBaseAccess.shareInstance(mContext).insertData(note);
    }

    public static void storeObject(Object o) {
        if (o instanceof Event) {
            storeEvent(JSONObject.toJSONString(o));
        } else if (o instanceof AppAction) {
            storeAppAction(JSONObject.toJSONString(o));
        } else if (o instanceof Page) {
            storePage(JSONObject.toJSONString(o));
        } else if (o instanceof ExceptionInfo) {
            storeException(JSONObject.toJSONString(o));
        }

    }

    public static synchronized void deleteData() {
        WriteDataBaseAccess.shareInstance(mContext).deleteAllNote();
    }


}

