package com.hk.simplenewsgong.simplegong.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.hk.simplenewsgong.simplegong.data.FragArticleTableContract.FragArticleEntry.CONTENT_URI;
import static com.hk.simplenewsgong.simplegong.data.FragArticleTableContract.FragArticleEntry.CONTENT_URI_CATEGORY;
import static com.hk.simplenewsgong.simplegong.data.FragArticleTableContract.FragArticleEntry.CONTENT_URI_ENTITY_NAMEENTRIES;
import static com.hk.simplenewsgong.simplegong.data.FragArticleTableContract.FragArticleEntry.CONTENT_URI_FIRSTSUBDOMAIN;
import static com.hk.simplenewsgong.simplegong.data.FragArticleTableContract.FragArticleEntry.CONTENT_URI_NAME;

/**
 * table contains the news article information
 * <p>
 * Created by simplegong
 */

public class FragArticleTableContract {
    public static final String CONTENT_AUTHORITY = "com.hk.simplenewsgong.simplegong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final int INDEX_RAWQUERY_PAGINATION_ID = 0;
    public static final int INDEX_RAWQUERY_PAGINATION_FINALURL = 1;
    public static final int INDEX_RAWQUERY_PAGINATION_TIMESTAMPONDOC = 2;
    public static final int INDEX_RAWQUERY_PAGINATION_TITLE = 3;
    public static final int INDEX_RAWQUERY_PAGINATION_IMAGEURL = 4;
    public static final int INDEX_RAWQUERY_PAGINATION_SIMILARITIESCOUNT = 5;
    public static final int INDEX_RAWQUERY_PAGINATION_ENTRY = 6;
    public static final int INDEX_RAWQUERY_PAGINATION_FIRSTSUBDOMAINTABLE_ID = 7;
    public static final int INDEX_RAWQUERY_PAGINATION_TIMESTAMPONDOCANDID = 8;
    public static final int INDEX_RAWQUERY_PAGINATION_ARTICLE_ID = 9;
    public static final int INDEX_RAWQUERY_SIGNAL_BOOKMARKALREADY = 10;
    public static final int INDEX_RAWQUERY_SIGNAL_READALREADY = 11;
    public static final int INDEX_RAWQUERY_FIRSTSUBDOMAIN_SOURCEICONURL = 12;
    public static final int INDEX_RAWQUERY_NAME = 13;
    public static final int INDEX_RAWQUERY_CATEGORYTABLE_ID = 14;
    public static final int INDEX_RAWQUERY_ENTITYNAME = 15;
    public static final int INDEX_RAWQUERY_ENTITYICONURL = 16;

    public static Map<Integer, List<Integer>> CATEGORY_FSD_MAP = new HashMap<>();
    public static Map<Integer, Map<String, String>> ENTITY_NAME_ICON_URL = new HashMap<>();

    static {
        //inserting fake data, each  number represent a firstsubdomain id
        List<Integer> listOfFSD = new ArrayList<>();
        listOfFSD.add(2);
        listOfFSD.add(6);
        listOfFSD.add(8);
        listOfFSD.add(17);
        listOfFSD.add(19);
        CATEGORY_FSD_MAP.put(2, listOfFSD);
        listOfFSD = new ArrayList<>();
        listOfFSD.add(1);
        listOfFSD.add(3);
        listOfFSD.add(7);
        listOfFSD.add(9);
        listOfFSD.add(10);
        listOfFSD.add(11);
        listOfFSD.add(13);
        listOfFSD.add(14);
        listOfFSD.add(15);
        listOfFSD.add(16);
        listOfFSD.add(18);
        CATEGORY_FSD_MAP.put(3, listOfFSD);
        listOfFSD = new ArrayList<>();
        listOfFSD.add(20);
        listOfFSD.add(22);
        listOfFSD.add(24);
        CATEGORY_FSD_MAP.put(5, listOfFSD);
        listOfFSD.add(21);
        listOfFSD.add(23);
        listOfFSD.add(25);
        listOfFSD.add(26);
        listOfFSD.add(33);
        CATEGORY_FSD_MAP.put(6, listOfFSD);
        listOfFSD = new ArrayList<>();
        listOfFSD.add(27);
        listOfFSD.add(28);
        listOfFSD.add(29);
        CATEGORY_FSD_MAP.put(7, listOfFSD);
        listOfFSD = new ArrayList<>();
        listOfFSD.add(30);
        listOfFSD.add(31);
        listOfFSD.add(32);
        CATEGORY_FSD_MAP.put(8, listOfFSD);


        //initial fake data for entity name and iconurl
        Map<String, String> entityinfo = new HashMap<String, String>();
        entityinfo.put("Padmé", "https://vignette.wikia.nocookie.net/theclonewiki/images/3/3e/Amidala_19.jpg/revision/latest?cb=20120714173832");
        ENTITY_NAME_ICON_URL.put(0, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Mace", "https://vignette.wikia.nocookie.net/theclonewiki/images/6/6f/WinduRyloth.jpg/revision/latest/scale-to-width-down/250?cb=20110715181053");
        ENTITY_NAME_ICON_URL.put(1, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Anakin", "https://vignette.wikia.nocookie.net/theclonewiki/images/b/b7/LibertyRescue-JC.png/revision/latest/scale-to-width-down/250?cb=20121108224858");
        ENTITY_NAME_ICON_URL.put(2, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Plo", "https://vignette.wikia.nocookie.net/theclonewiki/images/c/ce/Plo_Koon-TheLostOne.png/revision/latest/scale-to-width-down/250?cb=20150403235905");
        ENTITY_NAME_ICON_URL.put(3, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Shaak", "https://vignette.wikia.nocookie.net/theclonewiki/images/f/fb/ShaakTiCloneCadets.png/revision/latest/scale-to-width-down/250?cb=20150410213211");
        ENTITY_NAME_ICON_URL.put(4, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Yoda", "https://vignette.wikia.nocookie.net/theclonewiki/images/5/56/Yoda-Assassin.png/revision/latest/scale-to-width-down/222?cb=20111213011015");
        ENTITY_NAME_ICON_URL.put(5, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Ahsoka", "https://vignette.wikia.nocookie.net/theclonewiki/images/9/92/Ahsoka_evil.jpg/revision/latest/scale-to-width-down/640?cb=20120705172936");
        ENTITY_NAME_ICON_URL.put(6, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Barriss", "https://vignette.wikia.nocookie.net/theclonewiki/images/c/cb/Barriss_proof.jpg/revision/latest/scale-to-width-down/180?cb=20130225132843");
        ENTITY_NAME_ICON_URL.put(7, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Rex", "https://vignette.wikia.nocookie.net/theclonewiki/images/c/c8/RexCodyTheBadBatch.png/revision/latest/scale-to-width-down/250?cb=20150503140231");
        ENTITY_NAME_ICON_URL.put(8, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Maul", "https://vignette.wikia.nocookie.net/theclonewiki/images/1/18/I_am_counting_on_it.jpg/revision/latest/scale-to-width-down/160?cb=20130121160049");
        ENTITY_NAME_ICON_URL.put(9, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("Sidious", "https://vignette.wikia.nocookie.net/theclonewiki/images/6/64/InSidiousSmile-Revival.jpg/revision/latest/scale-to-width-down/200?cb=20121011001333");
        ENTITY_NAME_ICON_URL.put(10, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("R2D2", "https://vignette.wikia.nocookie.net/theclonewiki/images/8/88/R2OnVanqor-R2CH.jpg/revision/latest/scale-to-width-down/200?cb=20140310012453");
        ENTITY_NAME_ICON_URL.put(11, entityinfo);
        entityinfo = new HashMap<String, String>();
        entityinfo.put("C3PO", "https://vignette.wikia.nocookie.net/starwars/images/4/41/ThreepioIntrudes-TOTS.jpg/revision/latest?cb=20160220043636");
        ENTITY_NAME_ICON_URL.put(12, entityinfo);

    }

    public static Random random;
    public static int fake_articleid = 1;
    public static final String FAKE_FINALURL = "https://www.google.com";
    public static final String FAKE_IMAGEURL = "https://lh3.googleusercontent.com/4cXfm9YG59lys9woio9JM5qR_bOpCrv0dgJ1XmowbzgRpIzDRyNQQ8vB8yXsz3NQJ9Q=w300-rw";


    //public static final String RAWQUERY_ORDERSTRING = " order by fragarticle.timestampondocandid DESC , fragarticle.title DESC ";
    public static final String RAWQUERY_LIMITORDERSTRING = " LIMIT ?,? ";
    public static final String RAWQUERY_ORDERSTRING = " order by fragarticle.timestampondoc DESC, fragarticle.title DESC   ";
    public static final String RAWQUERY_FRAGARTICLE_WHEREIDSTRING = " fragarticle._id >= ? and fragarticle._id <= ? ";
    public static final String RAWQUERY_FRAGARTICLE_WHERENAMESTRING = " fragarticle.name = ? ";
    public static final String RAWQUERY_FRAGARTICLE_WHEREFIRSTSUBSTRING = " fragarticle.firstsubdomaintable_id == ?  ";
    public static final String RAWQUERY_FRAGARTICLE_WHERECATEGORYSTRING = " fragarticle.categorytable_id  = ?  ";
    public static final String RAWQUERY_FRAGARTICLE_WHEREENTITY_NAMESTRING = " fragarticle.entity_name = ?  ";
    public static final String RAWQUERY_FRAGARTICLE_GROUPBYENTITY_NAMESTRING = " group by fragarticle.entity_name ";
    public static final String RAWQUERY_FRAGARTICLE_SELECTIONSTRING =
            " select fragarticle._id as fragarticle_id, "
                    + " fragarticle.finalurl as fragarticle_finalurl, "
                    + " fragarticle.timestampondoc as fragarticle_timestampondoc, "
                    + " fragarticle.title as fragarticle_title, "
                    + " fragarticle.imageurl as fragarticle_imageurl, "
                    + " fragarticle.similiaritiescount as fragarticle_similaritiescount, "
                    + " fragarticle.entry as fragarticle_entry, "
                    + " fragarticle.firstsubdomaintable_id as fragarticle_firstsubdomaintable_id, "
                    + " fragarticle.timestampondocandid as fragarticle_timestampondocandid, "
                    + " fragarticle.article_id as fragarticle_article_id, "
                    + " signal.bookmarkalready as signal_bookmarkalready, "
                    + " signal.readalready as signal_readalready, "
                    + " firstsubdomain.sourceiconurl as firstsubdomain_sourceiconurl, "
                    + " fragarticle.name as fragarticle_name, "
                    + " fragarticle.categorytable_id as fragarticle_categorytable_id, "
                    + " fragarticle.entity_name as fragarticle_entity_name, "
                    + " fragarticle.entity_iconurl as fragarticle_entity_iconurl  "
                    + " from fragarticle "
                    + " left join firstsubdomain on fragarticle.firstsubdomaintable_id = firstsubdomain.firstsubdomaintable_id "
                    + " left join signal on fragarticle.article_id = signal.article_id ";
    public static final String RAWQUERY_FRAGARTICLE_RANGESTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + " where " + RAWQUERY_FRAGARTICLE_WHEREIDSTRING
            + RAWQUERY_ORDERSTRING;
    public static final String RAWQUERY_FRAGARTICLE_NAMESTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + " where " + RAWQUERY_FRAGARTICLE_WHERENAMESTRING
            + RAWQUERY_ORDERSTRING;
    public static final String RAWQUERY_FRAGARTICLE_CATEGORYSTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + " where " + RAWQUERY_FRAGARTICLE_WHERECATEGORYSTRING
            + RAWQUERY_ORDERSTRING;
    public static final String RAWQUERY_FRAGARTICLE_FIRSTSUBSTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + " where " + RAWQUERY_FRAGARTICLE_WHEREFIRSTSUBSTRING
            + RAWQUERY_ORDERSTRING;
    public static final String RAWQUERY_FRAGARTICLE_ENTITY_NAMELISTSTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + RAWQUERY_FRAGARTICLE_GROUPBYENTITY_NAMESTRING
            + RAWQUERY_ORDERSTRING;
    public static final String RAWQUERY_FRAGARTICLE_ENTITY_NAMEENTRIESSTRING = RAWQUERY_FRAGARTICLE_SELECTIONSTRING
            + " where " + RAWQUERY_FRAGARTICLE_WHEREENTITY_NAMESTRING
            + RAWQUERY_ORDERSTRING;


    public static final String RAWQUERY_BOOKMARK_ORDERSTRING = " order by fragarticle.timestampondoc DESC ";
    public static final String RAWQUERYBOOKMARKARTICLESELECTIONSTRING =
            " select "
                    + " fragarticle.article_id as fragarticle_article_id, "
                    + " fragarticle.firstsubdomaintable_id as fragarticle_firstsubdomaintable_id, "
                    + " fragarticle.finalurl as fragarticle_finalurl, "
                    + " fragarticle.timestampondoc as fragarticle_timestampondoc, "
                    + " fragarticle.title as fragarticle_title, "
                    + " fragarticle.imageurl as fragarticle_imageurl, "
                    + " signal.bookmarkalready as signal_bookmarkalready, "
                    + " signal.readalready as signal_readalready, "
                    + " firstsubdomain.sourceiconurl as firstsubdomain_sourceiconurl "
                    + " from fragarticle "
                    + " left join firstsubdomain on fragarticle.firstsubdomaintable_id = firstsubdomain.firstsubdomaintable_id "
                    + " left join signal on fragarticle.article_id = signal.article_id ";
    public static final String RAWQUERYBOOKMARKSTRING = RAWQUERYBOOKMARKARTICLESELECTIONSTRING
            + " where signal.bookmarkalready == 1 "
            + RAWQUERY_BOOKMARK_ORDERSTRING;
    public static final int INDEX_BOOKMARK_ARTICLETABLE_ID = 0;
    public static final int INDEX_BOOKMARK_FIRSTSUBDOMAINTABLE_ID = 1;
    public static final int INDEX_BOOKMARK_FINALURL = 2;
    public static final int INDEX_BOOKMARK_TIMESTAMPONDOC = 3;
    public static final int INDEX_BOOKMARK_TITLE = 4;
    public static final int INDEX_BOOKMARK_IMAGEURL = 5;
    public static final int INDEX_BOOKMARK_BOOKMARKALREADY = 6;
    public static final int INDEX_BOOKMARK_READALREADY = 7;
    public static final int INDEX_BOOKMARK_SOURCEICONURL = 8;


    public static final String PATH_FRAGARTICLE = "fragarticle";
    public static final String PATH_NAME = "name";
    public static final String PATH_FIRSTSUBDOMAIN = "firstsubdomain";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_ENTITY_NAMELIST = "entitynamelist";
    public static final String PATH_ENTITY_NAMEENTRIES = "entitynameentries";
    public static final String PATH_BOOKMARK = "bookmark";


    public static final class FragArticleEntry implements BaseColumns {

        //base URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .build();

        //URI for category name
        public static final Uri CONTENT_URI_NAME = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_NAME)
                .build();

        //URI for first subdomain reference
        public static final Uri CONTENT_URI_FIRSTSUBDOMAIN = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_FIRSTSUBDOMAIN)
                .build();

        //URI for category ID
        public static final Uri CONTENT_URI_CATEGORY = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_CATEGORY)
                .build();

        //URI for entity name list
        public static final Uri CONTENT_URI_ENTITY_NAMELIST = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_ENTITY_NAMELIST)
                .build();

        //URI for  entries of each entity
        public static final Uri CONTENT_URI_ENTITY_NAMEENTRIES = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_ENTITY_NAMEENTRIES)
                .build();

        //URI for bookmark list
        public static final Uri CONTENT_URI_BOOKMARK = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRAGARTICLE)
                .appendPath(PATH_BOOKMARK)
                .build();


        public static final String TABLE_NAME = "fragarticle";

        public static final String COLUMN_TIMESTAMPONDOC_AND_ID = "timestampondocandid";
        public static final String COLUMN_ARTICLEID = "article_id";
        public static final String COLUMN_TIMESTAMPONDOC = "timestampondoc";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FINALURL = "finalurl";
        public static final String COLUMN_FIRSTSUBDOMAINTABLE_ID = "firstsubdomaintable_id";
        public static final String COLUMN_IMAGEURL = "imageurl";
        public static final String COLUMN_SIMILARITIESCOUNT = "similiaritiescount";
        public static final String COLUMN_ENTRY = "entry";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORYTABLE_ID = "categorytable_id";
        public static final String COLUMN_ENTITY_NAME = "entity_name";
        public static final String COLUMN_ENTITY_ICONURL = "entity_iconurl";

    }

    /**
     * getting fake generated article id
     *
     * @return article id
     */
    public static int fake_getarticleid() {
        return fake_articleid++;
    }

    /**
     * getting fake generated timestamp
     *
     * @return timestamp
     */
    public static long fake_gettimestamp() {
        if (random == null) {
            random = new Random();
        }
        long epoch = ((System.currentTimeMillis() / 100000) * 100) + random.nextInt(100);
        return epoch;
    }

    /**
     * getting fake generated first subdomain id
     *
     * @return first subdomain id
     */
    public static int fake_getfirstsubdomainid() {
        if (random == null) {
            random = new Random();
        }
        int id = random.nextInt(31) + 1;
        return id;
    }

    /**
     * getting fake generated similiarities count
     *
     * @return similiarities counut
     */
    public static int fake_getsimCount() {
        if (random == null) {
            random = new Random();
        }
        int count = random.nextInt(15);
        return count;
    }

    /**
     * getting fake generated title
     *
     * @return title string
     */
    public static String fake_gettitle() {
        return "title: " + UUID.randomUUID().toString().substring(0, 10);
    }

    /**
     * getting fake generated category name
     *
     * @return category name
     */
    public static String fake_getname() {
        return "name_" + String.valueOf(random.nextInt(30));//random.ints(0,30).findFirst().getAsInt());
    }

    /**
     * getting fake generated entity info
     *
     * @return a map with key=entity name , value=the icon url
     */
    public static Map<String, String> fake_getEntityInfo() {
        if (random == null) {
            random = new Random();
        }
        return ENTITY_NAME_ICON_URL.get(random.nextInt(13));//random.ints(0,13).findFirst().getAsInt());
    }

    /**
     * getting fake generated entity ID
     *
     * @return entity id
     */
    public static int fake_getEntityID() {
        if (random == null) {
            random = new Random();
        }
        int count = random.nextInt(15); //random.ints(0, 15).findFirst().getAsInt();
        return count;
    }


    /**
     * return a constructed URI with timestampondocandid (input) Ex. timestampondocandid =
     * 154567889Z00000012
     *
     * @param timestampondocandid : encoded string like 154567889Z00000012
     * @return a constructed uri
     */
    public static Uri buildPaginationUriWithTimestampondocAndId(String timestampondocandid) {
        return CONTENT_URI.buildUpon()
                .appendPath(timestampondocandid)
                .build();
    }

    /**
     * return a decoded first part(154567889)
     * of the string from timestampondocandid (input)  Ex. timestampondocandid =
     * 154567889Z00000012
     *
     * @param timestampondocandid : encoded string like 154567889Z00000012
     * @return timestamp on the article after decoded
     */
    public static String decodeGetTimestampondoc(String timestampondocandid) {
        String timestamp = timestampondocandid.split("Z")[0];
        return timestamp;
    }

    /**
     * return a decoded second part(12)
     * of the string from timestampondocandid (input)  Ex. timestampondocandid =
     * 154567889Z00000012
     *
     * @param timestampondocandid : encoded string like 154567889Z00000012
     * @return article id after decoded
     */
    public static String decodeGetId(String timestampondocandid) {
        String id = timestampondocandid.split("Z")[1];
        return id;
    }


    /**
     * return a constructed URI with name (input) Ex. name=
     * hklatestnews
     *
     * @param name : category name
     * @return a constructed uri
     */
    public static Uri buildUriWithNamePathAndName(String name) {
        return CONTENT_URI_NAME.buildUpon()
                .appendPath(name)
                .build();
    }

    /**
     * return a constructed URI with first subdomain (input) Ex. id=1
     *
     * @param id : first subdomain id
     * @return a constructed uri
     */
    public static Uri buildUriWithFSD(int id) {
        return CONTENT_URI_FIRSTSUBDOMAIN.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
    }

    /**
     * return a constructed URI with category id (input) Ex. id=1
     *
     * @param id : category  id
     * @return a constructed uri
     */
    public static Uri buildUriWithCategory(int id) {
        return CONTENT_URI_CATEGORY.buildUpon()
                .appendPath(String.valueOf(id))
                .build();
    }

    /**
     * return a constructed URI with entity name(input) Ex. entryname=Sith
     *
     * @param entryname : entity name
     * @return a constructed uri
     */
    public static Uri buildUriWithNameEntries(String entryname) {
        return CONTENT_URI_ENTITY_NAMEENTRIES.buildUpon()
                .appendPath(String.valueOf(entryname))
                .build();
    }


}
