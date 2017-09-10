package com.example.MediaCounterApp.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Milan on 11/6/2016.
 */

/*
How to use:

run S.java in D:\DDocuments\Projects\MediaCounterApp\notes to generate the info to fill into fill();

MediaCounterDB.java:

    public void reload()
    {
        Reloader r = new Reloader();

        System.out.println(r.ti.size() + " " + r.ei.size());

        boolean res = true;
        for (Reloader.TitleInfo ti : r.ti)
        {
            res = addMedia(ti.name, ti.complete, ti.added);

            if (!res)
            {
                System.out.println("Problem! on [" + ti.name + ", " + ti.complete + ", " + ti.added + "]");
                break;
            }
        }

        if (res)
        {
            for (Reloader.EpisodeInfo ei : r.ei)
            {
                addEpisode(ei.name, ei.num, ei.date);
            }
        }
    }


MediaCounterActivity.java
    db.reload();
*/

public class Reloader
{
    public List<TitleInfo> ti;
    public List<EpisodeInfo> ei;

    public Reloader()
    {
        ti = new ArrayList<>();
        ei = new ArrayList<>();

        fill();
    }

    private void fill()
    {
        ti.add(new TitleInfo("Toradora", 0, false));
        ei.add(new EpisodeInfo("Toradora", 1, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Toradora", 2, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Toradora", 3, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Toradora", 4, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Toradora", 5, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Toradora", 6, "7-15-2016 22:06"));
        ei.add(new EpisodeInfo("Toradora", 7, "7-15-2016 22:30"));
        ei.add(new EpisodeInfo("Toradora", 8, "7-15-2016 22:55"));
        ei.add(new EpisodeInfo("Toradora", 9, "7-15-2016 23:19"));
        ei.add(new EpisodeInfo("Toradora", 10, "7-15-2016 23:49"));
        ei.add(new EpisodeInfo("Toradora", 11, "7-16-2016 00:19"));
        ei.add(new EpisodeInfo("Toradora", 12, "7-16-2016 00:43"));
        ei.add(new EpisodeInfo("Toradora", 13, "7-16-2016 01:07"));
        ei.add(new EpisodeInfo("Toradora", 14, "7-16-2016 10:30"));
        ei.add(new EpisodeInfo("Toradora", 15, "7-16-2016 10:54"));
        ei.add(new EpisodeInfo("Toradora", 16, "7-16-2016 11:18"));
        ei.add(new EpisodeInfo("Toradora", 17, "7-16-2016 11:43"));
        ei.add(new EpisodeInfo("Toradora", 18, "7-16-2016 12:07"));
        ei.add(new EpisodeInfo("Toradora", 19, "7-16-2016 12:31"));
        ei.add(new EpisodeInfo("Toradora", 20, "7-16-2016 13:05"));
        ei.add(new EpisodeInfo("Toradora", 21, "7-16-2016 13:30"));
        ei.add(new EpisodeInfo("Toradora", 22, "7-16-2016 13:57"));
        ei.add(new EpisodeInfo("Toradora", 23, "7-16-2016 14:21"));
        ei.add(new EpisodeInfo("Toradora", 24, "7-16-2016 14:44"));
        ei.add(new EpisodeInfo("Toradora", 25, "7-16-2016 15:06"));
        ei.add(new EpisodeInfo("Toradora", 26, "8-19-2016 22:53"));
        ti.add(new TitleInfo("Usagi Drop", 0, false));
        ei.add(new EpisodeInfo("Usagi Drop", 1, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 2, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 3, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 4, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 5, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 6, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 7, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 8, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 9, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 10, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Usagi Drop", 11, "7-7-2016 22:52"));
        ti.add(new TitleInfo("Amagi Brilliant Park", 0, false));
        ei.add(new EpisodeInfo("Amagi Brilliant Park", 1, "7-05-2016 22:42"));
        ti.add(new TitleInfo("Trigun", 0, false));
        ei.add(new EpisodeInfo("Trigun", 1, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 2, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 3, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 4, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 5, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 6, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Trigun", 7, "7-5-2016 21:16"));
        ti.add(new TitleInfo("Konosuba", 0, false));
        ei.add(new EpisodeInfo("Konosuba", 1, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Konosuba", 2, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Konosuba", 3, "10-16-2016 21:45"));
        ti.add(new TitleInfo("Re:Zero", 0, false));
        ei.add(new EpisodeInfo("Re:Zero", 1, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 2, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 3, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 4, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 5, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 6, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 7, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 8, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 9, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 10, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 11, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 12, "7-5-2016 21:16"));
        ei.add(new EpisodeInfo("Re:Zero", 13, "7-5-2016 21:49"));
        ei.add(new EpisodeInfo("Re:Zero", 14, "7-5-2016 22:43"));
        ei.add(new EpisodeInfo("Re:Zero", 15, "7-10-2016 23:07"));
        ei.add(new EpisodeInfo("Re:Zero", 16, "7-17-2016 22:53"));
        ei.add(new EpisodeInfo("Re:Zero", 17, "7-24-2016 12:44"));
        ei.add(new EpisodeInfo("Re:Zero", 18, "7-31-2016 19:59"));
        ei.add(new EpisodeInfo("Re:Zero", 19, "8-9-2016 20:54"));
        ei.add(new EpisodeInfo("Re:Zero", 20, "8-14-2016 14:32"));
        ei.add(new EpisodeInfo("Re:Zero", 21, "8-21-2016 20:47"));
        ei.add(new EpisodeInfo("Re:Zero", 22, "8-28-2016 13:31"));
        ei.add(new EpisodeInfo("Re:Zero", 23, "9-5-2016 20:41"));
        ei.add(new EpisodeInfo("Re:Zero", 24, "9-11-2016 12:01"));
        ei.add(new EpisodeInfo("Re:Zero", 25, "9-18-2016 15:05"));
        ti.add(new TitleInfo("Kabaneri", 0, false));
        ei.add(new EpisodeInfo("Kabaneri", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 5, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 6, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 7, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 8, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 9, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 10, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 11, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Kabaneri", 12, "7-7-2016 22:52"));
        ti.add(new TitleInfo("Boku no Hero", 0, false));
        ei.add(new EpisodeInfo("Boku no Hero", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Boku no Hero", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Boku no Hero", 3, "8-19-2016 22:01"));
        ei.add(new EpisodeInfo("Boku no Hero", 4, "8-19-2016 22:25"));
        ei.add(new EpisodeInfo("Boku no Hero", 5, "8-19-2016 22:53"));
        ei.add(new EpisodeInfo("Boku no Hero", 6, "8-20-2016 10:01"));
        ei.add(new EpisodeInfo("Boku no Hero", 7, "8-20-2016 10:26"));
        ti.add(new TitleInfo("Host Club", 0, false));
        ei.add(new EpisodeInfo("Host Club", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Host Club", 2, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Sakamoto", 0, false));
        ei.add(new EpisodeInfo("Sakamoto", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 5, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 6, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Sakamoto", 7, "7-5-2016 21:17"));
        ti.add(new TitleInfo("A Lull in the Sea", 0, false));
        ei.add(new EpisodeInfo("A Lull in the Sea", 1, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Netoge", 0, false));
        ei.add(new EpisodeInfo("Netoge", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 5, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 6, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Netoge", 7, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Dimension W", 0, false));
        ei.add(new EpisodeInfo("Dimension W", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 5, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 6, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 7, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 8, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 9, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 10, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 11, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Dimension W", 12, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Heavy Object", 0, false));
        ei.add(new EpisodeInfo("Heavy Object", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Heavy Object", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Heavy Object", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Heavy Object", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Heavy Object", 5, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Soul Eater", 0, false));
        ei.add(new EpisodeInfo("Soul Eater", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Soul Eater", 2, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Chuunibyou", 0, false));
        ei.add(new EpisodeInfo("Chuunibyou", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Chuunibyou", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Chuunibyou", 3, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Kimi ni Todoke S2", 0, false));
        ti.add(new TitleInfo("Erased", 0, false));
        ei.add(new EpisodeInfo("Erased", 1, "9-10-2016 16:18"));
        ei.add(new EpisodeInfo("Erased", 2, "9-10-2016 17:39"));
        ei.add(new EpisodeInfo("Erased", 3, "9-10-2016 18:06"));
        ei.add(new EpisodeInfo("Erased", 4, "9-11-2016 12:33"));
        ei.add(new EpisodeInfo("Erased", 5, "9-11-2016 13:02"));
        ei.add(new EpisodeInfo("Erased", 6, "9-11-2016 13:26"));
        ei.add(new EpisodeInfo("Erased", 7, "9-11-2016 13:59"));
        ei.add(new EpisodeInfo("Erased", 8, "9-11-2016 14:23"));
        ei.add(new EpisodeInfo("Erased", 9, "9-11-2016 14:46"));
        ei.add(new EpisodeInfo("Erased", 10, "9-11-2016 15:10"));
        ei.add(new EpisodeInfo("Erased", 11, "9-11-2016 15:36"));
        ei.add(new EpisodeInfo("Erased", 12, "9-11-2016 16:04"));
        ti.add(new TitleInfo("Planetes", 0, false));
        ti.add(new TitleInfo("Tokyo Ghoul", 0, false));
        ei.add(new EpisodeInfo("Tokyo Ghoul", 1, "9-3-2016 00:50"));
        ti.add(new TitleInfo("Death Parade", 0, false));
        ei.add(new EpisodeInfo("Death Parade", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Death Parade", 2, "7-5-2016 21:17"));
        ti.add(new TitleInfo("Katanagatari", 0, false));
        ti.add(new TitleInfo("Daredevil", 0, false));
        ti.add(new TitleInfo("Oremonogatari", 0, false));
        ti.add(new TitleInfo("Monogatari", 0, false));
        ti.add(new TitleInfo("Hanamonogatari", 0, false));
        ti.add(new TitleInfo("Madoka", 0, false));
        ei.add(new EpisodeInfo("Madoka", 1, "10-16-2016 14:05"));
        ti.add(new TitleInfo("GTO", 0, false));
        ti.add(new TitleInfo("Fate UBW", 0, false));
        ti.add(new TitleInfo("Fooly Cooly", 0, false));
        ti.add(new TitleInfo("Tamako Market", 0, false));
        ti.add(new TitleInfo("Seirei no Moribito", 0, false));
        ti.add(new TitleInfo("Ao Haru Ride", 0, false));
        ei.add(new EpisodeInfo("Ao Haru Ride", 1, "7-24-2016 13:53"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 2, "7-24-2016 14:17"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 3, "7-24-2016 22:38"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 4, "7-24-2016 23:02"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 5, "7-28-2016 21:32"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 6, "7-28-2016 21:56"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 7, "7-28-2016 22:20"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 8, "7-28-2016 22:45"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 9, "7-29-2016 22:16"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 10, "7-29-2016 22:45"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 11, "7-29-2016 23:12"));
        ei.add(new EpisodeInfo("Ao Haru Ride", 12, "7-29-2016 23:35"));
        ti.add(new TitleInfo("The Pet Girl of Sakurasou", 0, false));
        ei.add(new EpisodeInfo("The Pet Girl of Sakurasou", 1, "10-16-2016 11:16"));
        ti.add(new TitleInfo("The Anthem of the Heart", 0, false));
        ei.add(new EpisodeInfo("The Anthem of the Heart", 1, "7-9-2016 16:06"));
        ti.add(new TitleInfo("Food Wars", 0, false));
        ei.add(new EpisodeInfo("Food Wars", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 3, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 4, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 5, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 6, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 7, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 8, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Food Wars", 9, "7-6-2016 20:24"));
        ei.add(new EpisodeInfo("Food Wars", 10, "7-6-2016 20:52"));
        ei.add(new EpisodeInfo("Food Wars", 11, "7-6-2016 21:35"));
        ei.add(new EpisodeInfo("Food Wars", 12, "7-6-2016 22:09"));
        ei.add(new EpisodeInfo("Food Wars", 13, "7-9-2016 16:05"));
        ei.add(new EpisodeInfo("Food Wars", 14, "7-14-2016 20:12"));
        ei.add(new EpisodeInfo("Food Wars", 15, "7-14-2016 20:39"));
        ei.add(new EpisodeInfo("Food Wars", 16, "7-15-2016 19:12"));
        ei.add(new EpisodeInfo("Food Wars", 17, "7-15-2016 19:47"));
        ei.add(new EpisodeInfo("Food Wars", 18, "7-23-2016 13:34"));
        ei.add(new EpisodeInfo("Food Wars", 19, "7-23-2016 13:58"));
        ei.add(new EpisodeInfo("Food Wars", 20, "7-23-2016 15:09"));
        ei.add(new EpisodeInfo("Food Wars", 21, "7-23-2016 21:06"));
        ei.add(new EpisodeInfo("Food Wars", 22, "7-23-2016 21:47"));
        ei.add(new EpisodeInfo("Food Wars", 23, "8-20-2016 14:19"));
        ei.add(new EpisodeInfo("Food Wars", 24, "8-20-2016 14:54"));
        ti.add(new TitleInfo("Kiznaiver", 0, false));
        ti.add(new TitleInfo("Oreimo", 0, false));
        ti.add(new TitleInfo("Flying Witch", 0, false));
        ei.add(new EpisodeInfo("Flying Witch", 1, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 2, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 3, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 4, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 5, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 6, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 7, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 8, "UNKNOWN"));
        ei.add(new EpisodeInfo("Flying Witch", 9, "6-26-2016 14:22"));
        ei.add(new EpisodeInfo("Flying Witch", 10, "6-26-2016 14:47"));
        ei.add(new EpisodeInfo("Flying Witch", 11, "7-7-2016 22:53"));
        ei.add(new EpisodeInfo("Flying Witch", 12, "7-7-2016 22:53"));
        ti.add(new TitleInfo("Game of Thrones S5", 0, false));
        ei.add(new EpisodeInfo("Game of Thrones S5", 1, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 2, "7-5-2016 21:17"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 3, "7-8-2016 21:02"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 4, "7-8-2016 22:06"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 5, "7-8-2016 23:41"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 6, "7-9-2016 00:45"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 7, "7-9-2016 01:52"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 8, "7-11-2016 20:23"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 9, "7-12-2016 21:40"));
        ei.add(new EpisodeInfo("Game of Thrones S5", 10, "7-12-2016 22:52"));
        ti.add(new TitleInfo("Bananya", 0, false));
        ei.add(new EpisodeInfo("Bananya", 1, "7-7-2016 22:53"));
        ei.add(new EpisodeInfo("Bananya", 2, "7-24-2016 00:29"));
        ei.add(new EpisodeInfo("Bananya", 3, "7-24-2016 00:32"));
        ti.add(new TitleInfo("Terraformers", 0, false));
        ti.add(new TitleInfo("Nozaki-kun", 0, false));
        ti.add(new TitleInfo("Game of Thrones S6", 0, false));
        ei.add(new EpisodeInfo("Game of Thrones S6", 1, "8-5-2016 18:49"));
        ei.add(new EpisodeInfo("Game of Thrones S6", 2, "8-5-2016 19:49"));
        ei.add(new EpisodeInfo("Game of Thrones S6", 3, "8-5-2016 20:55"));
        ei.add(new EpisodeInfo("Game of Thrones S6", 4, "8-6-2016 21:42"));
        ei.add(new EpisodeInfo("Game of Thrones S6", 5, "8-6-2016 22:49"));
        ti.add(new TitleInfo("Silicon Valley S1", 0, false));
        ei.add(new EpisodeInfo("Silicon Valley S1", 1, "7-14-2016 21:41"));
        ei.add(new EpisodeInfo("Silicon Valley S1", 2, "7-14-2016 22:11"));
        ei.add(new EpisodeInfo("Silicon Valley S1", 3, "7-14-2016 22:43"));
        ei.add(new EpisodeInfo("Silicon Valley S1", 4, "7-17-2016 09:18"));
        ti.add(new TitleInfo("New Game", 0, false));
        ei.add(new EpisodeInfo("New Game", 1, "7-19-2016 22:47"));
        ei.add(new EpisodeInfo("New Game", 2, "7-21-2016 21:33"));
        ei.add(new EpisodeInfo("New Game", 3, "8-5-2016 11:15"));
        ei.add(new EpisodeInfo("New Game", 4, "8-5-2016 11:41"));
        ei.add(new EpisodeInfo("New Game", 5, "8-5-2016 12:07"));
        ei.add(new EpisodeInfo("New Game", 6, "8-11-2016 22:21"));
        ei.add(new EpisodeInfo("New Game", 7, "8-19-2016 21:15"));
        ei.add(new EpisodeInfo("New Game", 8, "10-28-2016 22:37"));
        ei.add(new EpisodeInfo("New Game", 9, "10-29-2016 19:46"));
        ei.add(new EpisodeInfo("New Game", 10, "10-29-2016 20:12"));
        ei.add(new EpisodeInfo("New Game", 11, "10-29-2016 20:40"));
        ei.add(new EpisodeInfo("New Game", 12, "10-29-2016 21:07"));
        ti.add(new TitleInfo("Mob Psycho 100", 0, false));
        ei.add(new EpisodeInfo("Mob Psycho 100", 1, "7-23-2016 22:54"));
        ti.add(new TitleInfo("The Art Club has a Problem!", 0, false));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 1, "7-23-2016 23:22"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 2, "7-23-2016 23:48"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 3, "7-24-2016 00:13"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 4, "7-30-2016 12:26"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 5, "8-5-2016 10:45"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 6, "8-11-2016 21:53"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 7, "8-18-2016 22:17"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 8, "8-27-2016 11:50"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 9, "9-2-2016 21:25"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 10, "9-10-2016 14:41"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 11, "9-16-2016 22:27"));
        ei.add(new EpisodeInfo("The Art Club has a Problem!", 12, "10-8-2016 23:49"));
        ti.add(new TitleInfo("Kimi No Na Wa", 0, false));
        ti.add(new TitleInfo("Hotarubi no Mori e", 0, false));
        ei.add(new EpisodeInfo("Hotarubi no Mori e", 1, "7-24-2016 08:53"));
        ti.add(new TitleInfo("Chihayafuru", 0, false));
        ti.add(new TitleInfo("The Girl Who Leapt Through Time", 0, false));
        ei.add(new EpisodeInfo("The Girl Who Leapt Through Time", 1, "7-30-2016 23:16"));
        ti.add(new TitleInfo("Kizumonogatari 1", 0, false));
        ei.add(new EpisodeInfo("Kizumonogatari 1", 1, "7-30-2016 21:31"));
        ti.add(new TitleInfo("Osomatsu", 0, false));
        ei.add(new EpisodeInfo("Osomatsu", 1, "7-31-2016 00:30"));
        ei.add(new EpisodeInfo("Osomatsu", 2, "7-31-2016 01:03"));
        ti.add(new TitleInfo("Welcome to NHK", 0, false));
        ei.add(new EpisodeInfo("Welcome to NHK", 1, "7-31-2016 01:29"));
        ti.add(new TitleInfo("D-Frag", 0, false));
        ei.add(new EpisodeInfo("D-Frag", 1, "7-31-2016 01:56"));
        ti.add(new TitleInfo("One Week Friends", 0, false));
        ei.add(new EpisodeInfo("One Week Friends", 1, "7-31-2016 13:21"));
        ei.add(new EpisodeInfo("One Week Friends", 2, "7-31-2016 13:45"));
        ei.add(new EpisodeInfo("One Week Friends", 3, "7-31-2016 14:09"));
        ei.add(new EpisodeInfo("One Week Friends", 4, "7-31-2016 14:33"));
        ei.add(new EpisodeInfo("One Week Friends", 5, "7-31-2016 15:38"));
        ei.add(new EpisodeInfo("One Week Friends", 6, "7-31-2016 16:04"));
        ei.add(new EpisodeInfo("One Week Friends", 7, "7-31-2016 16:31"));
        ei.add(new EpisodeInfo("One Week Friends", 8, "7-31-2016 17:01"));
        ei.add(new EpisodeInfo("One Week Friends", 9, "8-14-2016 15:14"));
        ei.add(new EpisodeInfo("One Week Friends", 10, "8-14-2016 15:41"));
        ei.add(new EpisodeInfo("One Week Friends", 11, "8-14-2016 16:05"));
        ei.add(new EpisodeInfo("One Week Friends", 12, "8-14-2016 16:30"));
        ti.add(new TitleInfo("Denki-gai", 0, false));
        ti.add(new TitleInfo("The Seven Deadly Sins", 0, false));
        ei.add(new EpisodeInfo("The Seven Deadly Sins", 1, "10-9-2016 18:43"));
        ti.add(new TitleInfo("GTO live action", 0, false));
        ei.add(new EpisodeInfo("GTO live action", 1, "8-21-2016 20:03"));
        ei.add(new EpisodeInfo("GTO live action", 2, "9-2-2016 23:42"));
        ti.add(new TitleInfo("Days", 0, false));
        ti.add(new TitleInfo("Kuruko's Basketball", 0, false));
        ei.add(new EpisodeInfo("Kuruko's Basketball", 1, "9-3-2016 00:16"));
        ei.add(new EpisodeInfo("Kuruko's Basketball", 2, "9-3-2016 13:19"));
        ti.add(new TitleInfo("Baby Steps", 0, false));
        ei.add(new EpisodeInfo("Baby Steps", 1, "9-3-2016 01:26"));
        ti.add(new TitleInfo("Yu Yu Hakusho", 0, false));
        ei.add(new EpisodeInfo("Yu Yu Hakusho", 1, "9-10-2016 15:24"));
        ti.add(new TitleInfo("Nisekoi", 0, false));
        ei.add(new EpisodeInfo("Nisekoi", 1, "9-16-2016 22:30"));
        ti.add(new TitleInfo("Kiss Him, Not Me!", 0, false));
        ei.add(new EpisodeInfo("Kiss Him, Not Me!", 1, "10-9-2016 00:21"));
        ti.add(new TitleInfo("Drifters", 0, false));
        ei.add(new EpisodeInfo("Drifters", 1, "10-15-2016 10:14"));
        ti.add(new TitleInfo("Occultic;Nine", 0, false));
        ei.add(new EpisodeInfo("Occultic;Nine", 1, "10-16-2016 10:41"));
        ti.add(new TitleInfo("Noragami", 0, false));
        ti.add(new TitleInfo("Shelter", 0, false));
        ei.add(new EpisodeInfo("Shelter", 1, "10-20-2016 00:36"));
        ti.add(new TitleInfo("Kizumonogatari 2", 0, false));
        ei.add(new EpisodeInfo("Kizumonogatari 2", 1, "10-24-2016 09:00"));
        ti.add(new TitleInfo("SNAFU ova", 0, false));
        ei.add(new EpisodeInfo("SNAFU ova", 1, "10-28-2016 22:04"));
        ti.add(new TitleInfo("My Little Monster", 0, false));
        ei.add(new EpisodeInfo("My Little Monster", 1, "10-29-2016 21:54"));
        ei.add(new EpisodeInfo("My Little Monster", 2, "10-29-2016 22:24"));
        ti.add(new TitleInfo("Keijo", 0, false));
        ei.add(new EpisodeInfo("Keijo", 1, "11-5-2016 19:34"));
        ei.add(new EpisodeInfo("Keijo", 2, "11-5-2016 20:04"));
        ei.add(new EpisodeInfo("Keijo", 3, "11-5-2016 20:31"));
        ei.add(new EpisodeInfo("Keijo", 4, "11-5-2016 20:57"));
        ei.add(new EpisodeInfo("Keijo", 5, "11-5-2016 21:29"));
    }

    public class TitleInfo
    {
        public String name;
        public boolean complete;
        public long added;

        public TitleInfo(String n, int x, boolean c)
        {
            name = n;
            complete = c;
            added = 0;
        }
    }

    public class EpisodeInfo
    {
        public String name;
        public int num;
        public long date;

        public EpisodeInfo(String n, int nu, String d)
        {
            name = n;
            num = nu;
            date = convertDate(d);
            System.out.println("[" + d + "] -> " + date);
        }

        private long convertDate(String d)
        {
            // "11-5-2016 19:34"

            long result = 0;

            if (d.equals("UNKNOWN"))
            {
                return result;
            }
            else
            {
                try
                {
                    String first[] = d.split(" ");

                    String datePieces[] = first[0].split("-");

                    String timePieces[] = first[1].split(":");

                    Calendar c = Calendar.getInstance();

                    c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datePieces[1]));
                    c.set(Calendar.MONTH, Integer.parseInt(datePieces[0]) - 1);
                    c.set(Calendar.YEAR, Integer.parseInt(datePieces[2]));
                    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timePieces[0]));
                    c.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    result = c.getTimeInMillis();
                }
                catch (Exception e)
                {
                    return 0;
                }

            }
            return result;
        }
    }
}
