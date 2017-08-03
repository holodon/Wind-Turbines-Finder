package vladimir.apps.dwts.anlagensuche;

/**
 *
 * WEA Suche
 *
 * @author
 *      Vladimir (jelezarov.vladimir@gmail.com)
 */

class dataWEA {
    private String breit;
    private String lang;
    private String desc1;
    private String desc2;

    dataWEA(String breit, String lang, String desc1, String desc2) {
        this.breit = breit;
        this.lang = lang;
        this.desc1 = desc1;
        this.desc2 = desc2;
    }
    String getDesc() {
        return (desc2.length()==0? desc1 : (desc1 + " | " + desc2));
    }
    String getBreit() {
        return breit;
    }
    String getLang() {
        return lang;
    }
}
