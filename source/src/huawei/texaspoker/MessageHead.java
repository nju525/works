package huawei.texaspoker;

import java.util.HashMap;
import java.util.Map;

public class MessageHead {
	 public final String SEAT="seat"; //座次消息
	 public final String GAME_OVER="game-over"; //游戏结束消息
	 public final String BINLD="blind"; //盲注消息
	 public final String HOLD="hold"; //手牌消息
	 public final String INQUIRE="inquire"; //询问消息
	 public final String FLOP="flop"; //公牌消息
	 public final String TURN="turn"; //转牌消息
	 public final String RIVER="river"; //河牌消息
	 public final String SHUTDOWN="showdown"; //摊牌消息
	 public final String POT_WIN="pot-win"; //奖池分配
	 public final Map<String, Integer> map=new HashMap<String, Integer>();
	 {
		 map.put(SEAT, 1);
		 map.put(BINLD, 2);
		 map.put(HOLD, 3);
		 map.put(INQUIRE, 4);
		 map.put(FLOP, 5);
		 map.put(TURN, 6);
		 map.put(RIVER, 7);
		 map.put(SHUTDOWN, 8);
		 map.put(POT_WIN, 9);
		 map.put(GAME_OVER, 10);
	 }
}
