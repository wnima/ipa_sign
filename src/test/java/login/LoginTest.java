package login;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.utils.ExcelUtil;
import com.bootdo.common.utils.IPUtils;
import com.bootdo.common.utils.MD5Utils;
import com.bootdo.signature.exception.APIException;
import com.bootdo.signature.util.ITSUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import net.sf.json.JSON;

public class LoginTest {

	//@Test
	public void test() {
		String pwd = MD5Utils.encrypt("admin", "88888888");
		System.out.println(pwd);
		
		
	}
	
	public void excelRead() {
		try {
			List<List<String>> result = ExcelUtil.readExcel(new FileInputStream("D:/test.xls"));
			result.forEach(i->System.out.println(i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void token() throws APIException{
		Map headMap = ITSUtils.getToken("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgrHhmSSh5vcprw8hy\r\n" + 
				"/qwRKxx8YIqdXTlUnKtGYgiUQ4KgCgYIKoZIzj0DAQehRANCAAQ9cN7coOty+HdK\r\n" + 
				"61aPIIgJaeY5NiK4ucAYE1AhMjFf/AuvVbNIxq9CTN+ge3EXI1FzNMjgTsXZWAt4\r\n" + 
				"WVFpHowF", "e588d3a7-94d4-419a-b40f-c98ecf1293b6", "96PD5XV84H");
		List<Map<String, Object>> des = ITSUtils.getDevices(headMap);
		System.out.println(des);
	}

}

 class Item {
    private int bei;
    private String choseitem;
    private String chosename;
    private int i;
    private int j;
    private int k;
    private String listname;
    private String matchname;
    private String mid;
    private String odds;
    private String oddsname;
    private String zid;
    public void setBei(int bei) {
         this.bei = bei;
     }
     public int getBei() {
         return bei;
     }

    public void setChoseitem(String choseitem) {
         this.choseitem = choseitem;
     }
     public String getChoseitem() {
         return choseitem;
     }

    public void setChosename(String chosename) {
         this.chosename = chosename;
     }
     public String getChosename() {
         return chosename;
     }

    public void setI(int i) {
         this.i = i;
     }
     public int getI() {
         return i;
     }

    public void setJ(int j) {
         this.j = j;
     }
     public int getJ() {
         return j;
     }

    public void setK(int k) {
         this.k = k;
     }
     public int getK() {
         return k;
     }

    public void setListname(String listname) {
         this.listname = listname;
     }
     public String getListname() {
         return listname;
     }

    public void setMatchname(String matchname) {
         this.matchname = matchname;
     }
     public String getMatchname() {
         return matchname;
     }

    public void setMid(String mid) {
         this.mid = mid;
     }
     public String getMid() {
         return mid;
     }

    public void setOdds(String odds) {
         this.odds = odds;
     }
     public String getOdds() {
         return odds;
     }

    public void setOddsname(String oddsname) {
         this.oddsname = oddsname;
     }
     public String getOddsname() {
         return oddsname;
     }

    public void setZid(String zid) {
         this.zid = zid;
     }
     public String getZid() {
         return zid;
     }
	@Override
	public String toString() {
		return "Item [bei=" + bei + ", choseitem=" + choseitem + ", chosename=" + chosename + ", i=" + i + ", j=" + j
				+ ", k=" + k + ", listname=" + listname + ", matchname=" + matchname + ", mid=" + mid + ", odds=" + odds
				+ ", oddsname=" + oddsname + ", zid=" + zid + "]";
	}

}

 class Items {

    private int betmulriple;
    private List<Item> item;
    public void setBetmulriple(int betmulriple) {
         this.betmulriple = betmulriple;
     }
     public int getBetmulriple() {
         return betmulriple;
     }

    public void setItem(List<Item> item) {
         this.item = item;
     }
     public List<Item> getItem() {
         return item;
     }
	@Override
	public String toString() {
		return "Items [betmulriple=" + betmulriple + ", item=" + item + "]";
	}

}

 class Match {

    private int dan;
    private String mid;
    private List<String> oddsname;
    private String zid;
    public void setDan(int dan) {
         this.dan = dan;
     }
     public int getDan() {
         return dan;
     }

    public void setMid(String mid) {
         this.mid = mid;
     }
     public String getMid() {
         return mid;
     }

    public void setOddsname(List<String> oddsname) {
         this.oddsname = oddsname;
     }
     public List<String> getOddsname() {
         return oddsname;
     }

    public void setZid(String zid) {
         this.zid = zid;
     }
     public String getZid() {
         return zid;
     }
	@Override
	public String toString() {
		return "Match [dan=" + dan + ", mid=" + mid + ", oddsname=" + oddsname + ", zid=" + zid + "]";
	}

}

 class JsonRootBean {

    private List<Items> items;
    private List<Match> match;
    private String playmethodid;
    private String theoreticalbonus;
    private List<String> type;
    public void setItems(List<Items> items) {
         this.items = items;
     }
     public List<Items> getItems() {
         return items;
     }

    public void setMatch(List<Match> match) {
         this.match = match;
     }
     public List<Match> getMatch() {
         return match;
     }

    public void setPlaymethodid(String playmethodid) {
         this.playmethodid = playmethodid;
     }
     public String getPlaymethodid() {
         return playmethodid;
     }

    public void setTheoreticalbonus(String theoreticalbonus) {
         this.theoreticalbonus = theoreticalbonus;
     }
     public String getTheoreticalbonus() {
         return theoreticalbonus;
     }

    public void setType(List<String> type) {
         this.type = type;
     }
     public List<String> getType() {
         return type;
     }
	@Override
	public String toString() {
		return "JsonRootBean [items=" + items + ", match=" + match + ", playmethodid=" + playmethodid
				+ ", theoreticalbonus=" + theoreticalbonus + ", type=" + type + "]";
	}
}