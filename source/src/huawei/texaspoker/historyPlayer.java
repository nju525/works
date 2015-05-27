package huawei.texaspoker;

/**
 * Player类  用来存储牌手的历史信息
 */
public class historyPlayer {
	public int pid;//牌手ID
	public int joinPot;//入池数
	
	public int preFlop_call;//preFlop动作
	public int preFlop_bet;
	
	public int Flop_call;//flop动作
	public int Flop_bet;
	public int Flop_fold;
	
	public int Turn_call;//turn动作
	public int Turn_bet;
	public int Turn_fold;
	
	public int River_call;//river动作
	public int River_bet;
	public int River_fold;
	
	public int game_win;//赢牌次数
	
	public int game_showdownWin;//摊牌胜利次数
}
