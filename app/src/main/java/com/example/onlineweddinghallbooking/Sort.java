package com.example.onlineweddinghallbooking;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Sort {

    public ArrayList<Venue> selectionSort(ArrayList<Venue> venueArrayList)
    {
        int pos;
        Venue tempVenue;


        if (venueArrayList != null)
        {
            for (int i = 0; i < venueArrayList.size(); i++)
            {
                pos = i;

                for (int j = i+1; j < venueArrayList.size(); j++)
                {
                    if (venueArrayList.get(j).getNoOfBookings() > venueArrayList.get(pos).getNoOfBookings())
                    {
                        pos = j;
                    }
                }

                tempVenue = venueArrayList.get(pos);

                final Venue iElement = venueArrayList.get(i);
                venueArrayList.remove(pos);
                venueArrayList.add(pos, iElement);

                venueArrayList.remove(i);
                venueArrayList.add(i, tempVenue);

            }

            return venueArrayList;
        }

        return null;

    }

}
