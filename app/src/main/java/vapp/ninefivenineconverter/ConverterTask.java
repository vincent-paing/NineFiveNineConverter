package vapp.ninefivenineconverter;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.*;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.*;

/**
 * Created by Vincent on 10-Jan-15.
 */
public class ConverterTask extends AsyncTask {

    private Context context;
    private String[] rawContactsNumber;
    private int actionCode;
    private ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

    public ConverterTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Start", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        final String[] projection = new String[] {
                RawContacts.CONTACT_ID, // the contact id column
                RawContacts.DELETED     // column if this contact is deleted
        };
        final Cursor rawContacts = context.getContentResolver().query(
                RawContacts.CONTENT_URI,    // the URI for raw contact provider
                projection,
                null,                    // selection = null, retrieve all entries
                null,                    // selection is without parameters
                null);					// do not order
        final int contactIdColumnIndex = rawContacts.getColumnIndex(RawContacts.CONTACT_ID);

        final int deletedColumnIndex = rawContacts.getColumnIndex(RawContacts.DELETED);

        if(rawContacts.moveToFirst()) {
            while(!rawContacts.isAfterLast()) {		// still a valid entry left?
                final int contactId = rawContacts.getInt(contactIdColumnIndex);
                final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);
                if(!deleted) {
                    EditContactPhone(contactId);
                }
                rawContacts.moveToNext();			// move to the next entry
            }
        }
        rawContacts.close();
        if (ops!=null) {
            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void EditContactPhone(int contactId) {
        final String[] projection = new String[] {
                Phone.NUMBER,	// the name of the contact
                Phone.TYPE		// the id of the column in the data table for the image
        };
        final Cursor phone =  context.getContentResolver().query(
                Phone.CONTENT_URI,
                projection,
                Data.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)},
                null);
        if(phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final int phoneType = phone.getInt(contactTypeColumnIndex);
                String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='"  +
                        Phone.CONTENT_ITEM_TYPE + "'" + " AND " + Phone.TYPE + "=?";
                String[] phoneArgs = new String[]{String.valueOf(contactId), String.valueOf(phoneType)};
                String newNumber = null;
                switch (actionCode) {
                    case (0) :
                        newNumber = buildNineFineNine(number);
                        ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                                .withSelection(selectPhone, phoneArgs)
                                .withValue(Phone.NUMBER, newNumber)
                                .build());
                        phone.moveToNext();
                        break;
                    case (1):
                        newNumber = buildNineFineNine(number);
                        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValue(Data.RAW_CONTACT_ID, contactId)
                                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                                .withValue(Phone.NUMBER, newNumber)
                                .withValue(Phone.TYPE, Phone.TYPE_CUSTOM)
                                .withValue(Phone.LABEL, "NineFiveNine")
                                .build());
                        phone.moveToNext();
                        break;
                    case (2) :
                        newNumber = buildZeroNine(number);
                        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValue(Data.RAW_CONTACT_ID, contactId)
                                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                                .withValue(Phone.NUMBER, newNumber)
                                .withValue(Phone.TYPE, Phone.TYPE_CUSTOM)
                                .withValue(Phone.LABEL, "ZeroNine")
                                .build());
                        phone.moveToNext();
                        break;
                }

            }
        }
        phone.close();
    }

    private String buildZeroNine(String number) {
        if (number.substring(0, 4).equals("+959")) {
            String temp = number.substring(4);
            return "09".concat(temp);
        } else {
            return number;
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Toast.makeText(context, "Finish Converting", Toast.LENGTH_LONG).show();
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public String buildNineFineNine(String number) {
        if (number.substring(0, 2).equals("09")) {
            String temp = number.substring(2);
            return "+959".concat(temp);
        } else {
            return number;
        }
    }
}
