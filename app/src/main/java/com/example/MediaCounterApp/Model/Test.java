package com.example.MediaCounterApp.Model;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by milan on 12/11/2016.
 */

/*
from:
Ao Haru Ride				-1
	7-24-2016 13:53
	7-24-2016 14:17
	7-24-2016 22:38
	7-24-2016 23:02
	7-28-2016 21:32
	7-28-2016 21:56
	7-28-2016 22:20
	7-28-2016 22:45
	7-29-2016 22:16
	7-29-2016 22:45
	7-29-2016 23:12
	7-29-2016 23:35


to:
	{
		title:"chitoge",
		complete_status:0,
		added_date:1481256811603,
		episodes:
			[
				1481256814366,
				1481258066466,
				1481259686228,
				1481261385842,
				1481263312326
			]
	},
 */
public class Test
{
    public static void main(String[] args)
    {
        //fileTest();
        //buildTest();
        buildTest1();
    }

    public static void buildTest1()
    {
        int bufSize = 10;
        int[] buf = new int[bufSize];
        for (int k = 0; k < buf.length; k++)
        {
            buf[k] = -1;
        }
        List<Integer> l = new ArrayList<Integer>();

        int[] data = new int[]
        {
            10, 5,
            20, 15,
            50, 3,
            75, 18,
            96, 6,
            100, 57
        };

        int factor = 100;

        int curIdx = 0;

        int prevX = 0;
        int prevY = 0;

        // Set the first point
        buf[curIdx] = prevY;
        curIdx++;
        l.add(0);
        for (int i = 0; i < data.length / 2; i++)
        {
            int newX = data[2 * i];
            int newY = data[2 * i + 1];

            int dy = (newY - prevY);
            int dx = (newX - prevX);

            int step = dy * factor / dx;
            System.out.printf("\t(%d %d) -> (%d %d), slope %d, curIdx %d\n", prevX, prevY, newX, newY, step, curIdx);

            int pos = prevY * factor;
            for (int j = prevX + 1; j <= newX; j++)
            {
                pos += step;
                buf[curIdx] = pos / factor;
                l.add(pos / factor);
                curIdx++;

                if (curIdx == bufSize)
                {
                    print(buf, buf.length);
                    curIdx = 0;
                    for (int k = 0; k < buf.length; k++)
                    {
                        buf[k] = -1;
                    }
                }
            }

            prevX = newX;
            prevY = newY;
        }

        if (curIdx > 0)
        {
            print(buf, curIdx);
        }

        System.out.println(l);
    }

    public static void buildTest()
    {
        int bufSize = 10;
        int[] buf = new int[bufSize];

        int[] data = new int[]{12, 3, 24, 18, 11, 6, 1, 7, 13};

        int curIdx = 0;
        for (int i = 0; i < data.length; i++)
        {
            int val = i;
            int len = data[i];

            while (len > 0)
            {
                int toWrite = (len > bufSize - curIdx) ? bufSize - curIdx : len;
                //System.out.printf("i %d, writing %d of %d (%d remaining)\n", i, toWrite, val, len);

                copy(buf, val, curIdx, curIdx + toWrite);
                curIdx += toWrite;
                len -= toWrite;

                if (curIdx == bufSize)
                {
                    print(buf, buf.length);
                    curIdx = 0;
                }
            }
        }

        if (curIdx > 0)
        {
            print(buf, curIdx);
        }
    }

    public static void copy(int[] buf, int val, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            buf[i] = val;
        }
    }

    public static void print(int[] buf, int len)
    {
        System.out.print("[");
        for (int i = 0; i < len; i++)
        {
            System.out.print(buf[i] + " ");
        }
        System.out.println("]");
    }

    public static void fileTest()
    {
        try
        {
            Scanner s = new Scanner(new File("C:/users/milan/desktop/media_counter_data.txt"));
            PrintStream output = new PrintStream(new File("media_counter_import.txt"));

            boolean firstTitle = true;
            boolean firstDate = true;
            int datesPrinted = 0;

            output.println("[");
            while (s.hasNextLine())
            {
                String line = s.nextLine();

                boolean title = !line.startsWith("\t");
                System.out.println(title + " [" + line + "]");
                if (title)
                {
                    if (!firstDate || (datesPrinted == 0 && !firstTitle))
                    {
                        output.println("]\n\t},");
                    }
                    // Title
                    output.println("\t{");
                    String[] linePieces = line.split("\t");
                    String titleText = linePieces[0].trim();
                    output.println("\t\ttitle:\"" + titleText + "\",");
                    output.println("\t\tcomplete_status:0,");
                    long added = convertDate(linePieces[linePieces.length - 1].trim());
                    output.println("\t\tadded_date:" + added + ",");
                    System.out.println(Arrays.toString(linePieces));
                    firstDate = true;
                    output.print("\t\tepisodes:[");
                    datesPrinted = 0;
                    firstTitle = false;
                }
                else
                {
                    // It's a date line
                    datesPrinted++;
                    long timestampe = convertDate(line);
                    if (!firstDate)
                    {
                        output.print(",");
                    }
                    firstDate = false;
                    output.print(convertDate(line));

                }
            }
            output.println("]\n\t},");
            output.println("]");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static long convertDate(String date)
    {
        Calendar c = Calendar.getInstance();

        date = date.trim();

        try
        {
            String[] pieces = date.split(" ");

            String[] dateText = pieces[0].split("-");
            c.set(Calendar.MONTH, Integer.parseInt(dateText[0]) - 1);
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateText[1]));
            c.set(Calendar.YEAR, Integer.parseInt(dateText[2]));

            String[] time = pieces[1].split(":");
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);

            return c.getTimeInMillis();
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
