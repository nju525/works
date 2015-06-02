

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Game {
	private String MsgHead;
	Socket player;
	PrintWriter player2server;
	BufferedReader reader;
	MessageHead MsgHeadHanlder;
	Desk desk;
	int inquirecount=1;//记录当前牌局状态是第几轮inquire。
	
	String serverip,myip;
	int serverport,myport;  
	    
	int myorder;
	int mypid;
	int mybet;//当前这一局已经投入的筹码数
	private boolean isDiscard;//记录自己是否弃牌
	private List<Card> holdCards;//自己手牌
	private int mymoney,myjetton;//自己的筹码和金额
	Map<Integer, Opponent> Pid_Opponent;//根据pid储存一个对手实例。
	public Game(){
		isDiscard=false;
		holdCards=new ArrayList<Card>(2);
		Pid_Opponent=new HashMap<Integer, Opponent>();
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub	
		//初始化
		Game dsnju=new Game();
		
		dsnju.serverip=args[0];
		dsnju.serverport=new Integer(args[1]);
		dsnju.myip=args[2];
		dsnju.myport=new Integer(args[3]);
		dsnju.mypid=new Integer(args[4]);
		
		/*dsnju.serverip="127.0.0.1";
		dsnju.serverport=8888;
		dsnju.myip="127.0.0.1";
		dsnju.myport=4533;
		dsnju.mypid=1111;*/
		
		dsnju.initialize(args);
		//发送注册信息
		dsnju.player2server.println("reg: "+dsnju.mypid+" DSNJU ");
		dsnju.player2server.flush();
		//牌局计数
		int count=0;
		start:while(true){//持续进行，直到收到game-over消息
			do{
				dsnju.getAllMsg(dsnju.reader);
				
			}while(!dsnju.MsgHead.equals("pot-win")&&!dsnju.MsgHead.equals("game-over "));
			if(dsnju.MsgHead.equals("game-over "))//game-over不带“/”解析出来的head包含空格
				break start;
			//对一些变量清空
			dsnju.Pid_Opponent.clear();
			dsnju.holdCards.clear();//清空自己的手牌列表
			dsnju.inquirecount=1;
			dsnju.isDiscard=false;
			dsnju.mybet=0;
			//清空每个对手对象的动作Map
			/*for(Entry<Integer, Opponent> entry:dsnju.Pid_Opponent.entrySet()){
				entry.getValue().action.clear();
			}*/	
			if(!dsnju.player.isConnected())
				dsnju.player.connect(new InetSocketAddress(dsnju.serverip, dsnju.serverport));//连接server
			count++;
			//System.out.println(dsnju.mypid+"'s "+count+" round is over");
		}
		dsnju.reader.close();
		dsnju.player2server.close();
		dsnju.player.close();
	}
	private void initialize(String args[]) throws UnknownHostException, IOException{
		boolean connected=false;
		while(!connected){
			try {
				SocketAddress serveraddress = new InetSocketAddress(
						args[0], Integer.parseInt(args[1]));
				SocketAddress hostaddress = new InetSocketAddress(args[2],
						Integer.parseInt(args[3]));

				player = new Socket();
				player.setReuseAddress(true);
				player.bind(hostaddress);//绑定客户端到指定IP和端口号
				player.connect(serveraddress);
				connected=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				continue;
			}//连接server
		}
		/*player=new Socket(serverip, serverport);*/
		player2server=new PrintWriter(player.getOutputStream());
		reader=new BufferedReader(new InputStreamReader(player.getInputStream()));
		MsgHeadHanlder=new MessageHead();
		//System.out.println("已初始化");
	}
	/**
	 * 获取消息体 并对MsgHead赋值
	 * @param reader
	 * @param msghead
	 * @return
	 * @throws IOException
	 */
	public  void getAllMsg(BufferedReader reader) throws IOException{
		//不能将MsgHead作为参数带入使其指向新的对象。因为对象传参也是传的备份，指向新对象后就不是原来的引用
		//msghead=new String(result.substring(0, head.indexOf("/")));

		String head=reader.readLine();//获取消息头
		if(head.equals("game-over ")){//game-over消息不带/
			this.setMsgHead(head);
			return;
		}
		StringBuffer result=new StringBuffer(head);	
		head=result.substring(0, head.indexOf("/"));//去除“/”
		/*result.insert(0, "/");
		head=result.substring(0, head.length());//在head前插入/，去掉尾部的/，若/后有空格用此方法*/
		
		this.setMsgHead(head);
		this.HandleMsg(MsgHead);
	}
	public void setMsgHead(String msghead){
		this.MsgHead=new String(msghead);
	}
	public void HandleMsg(String msghead) throws IOException{
		if(msghead==null)
			return;
		int label=MsgHeadHanlder.map.get(msghead);
		String head="/"+msghead+" ";//每一个指令都带空格
	switch (label) {
		case 1:
			//调用处理座次信息方法
			//System.out.println("……处理座次信息……");
			HanldeSeat(reader, head);
			break;
		case 2:
			//调用处理盲注信息方法
			//System.out.println("……处理盲注信息……");
			HandleBlind(reader, head);
			break;
		case 3:
			//调用处理手牌信息方法
			//System.out.println("……处理手牌信息……");
			HanldeHoldCards(reader, head);
			break;
		case 4:
			//调用处理询问信息方法
			//System.out.println("……处理询问信息……");
			HanldeInquire(reader, head);
			break;
		case 5:
			//调用处理公牌信息方法
			//System.out.println("……处理公牌信息……");
			HandleFlop(reader, head);
			break;
		case 6:
			//调用处理转牌信息方法
			//System.out.println("……处理转牌信息……");
			HandleTurn(reader, head);
			break;
		case 7:
			//调用处理河牌信息方法
			//System.out.println("……处理河牌信息……");
			HandleRiver(reader, head);
			break;
		case 8:
			//调用处理摊牌信息方法
			//System.out.println("……处理摊牌信息……");
			HandleShowdown(reader, head);			
			break;
		case 9:
			//调用处理奖池信息方法
			//System.out.println("……处理奖池信息……");
			HandlePot_Win(reader, head);
			break;
		case 10:
			//调用处理结束信息方法
			break;
		default:
			break;
		}
	}
		
	/**
	 * 处理座次信息，记录大小盲等
	 * 根据传入的消息体提取信息
	 * 创建对手对象存入Map
	 * 记录执行顺序
	 * @throws IOException 
	 */
	public void HanldeSeat(BufferedReader reader,String head) throws IOException{
		int money,jetton,pid;//记录每一位玩家的筹码和金币数		
		desk=new Desk(0, 0, 0);//初始化desk
		desk.playercount=0;//记录座次消息的第几行，即本局参与玩家数
		String temp="";
		int linecount=0;
		while(!(temp=reader.readLine()).equals(head)){
			String splittemp[]=temp.split(" ");//并以空格为分隔符分成数组			
			switch (linecount) {
			case 0://button
				pid=new Integer(splittemp[1]);
				jetton=new Integer(splittemp[2]).intValue();//获得该玩家jetton
				money=new Integer(splittemp[3]).intValue();				
				desk.setButton(pid);
				break;
			case 1://小盲
				pid=new Integer(splittemp[2]);
				jetton=new Integer(splittemp[3]).intValue();
				money=new Integer(splittemp[4]).intValue();				
				desk.setSmallBlind(pid);
			case 2://大盲——只有两个玩家时没有大盲，只有button和小盲
				pid=new Integer(splittemp[2]);
				jetton=new Integer(splittemp[3]).intValue();
				money=new Integer(splittemp[4]).intValue();				
				desk.setBigBlind(pid);
				break;
			default:
				pid=new Integer(splittemp[0]).intValue();
				jetton=new Integer(splittemp[1]).intValue();
				money=new Integer(splittemp[2]).intValue();
				break;
			}
			if(pid!=mypid)
				Pid_Opponent.put(pid, new Opponent(pid, jetton, money,linecount));//创建对手对象
			setMyself(pid, jetton, money,linecount);//设置自己的jetton、money即执行次序
			linecount++;
		}		
		desk.playercount=linecount;
		Opponent o=Pid_Opponent.get(desk.getButton());
		if(o!=null)
			o.order=linecount;//对button位的次序重新设为玩家人数
		else//o为空，说明自己是button位，次序为最后
			this.myorder=desk.playercount;
		/*if(desk.getButton()==mypid)//
			this.myorder=desk.playercount;*/
		desk.setcardStatus(0);//设置牌局状态
	}
	
	private int setMyself(int pid,int jetton,int money,int linecount){
		if(pid!=mypid)
			return -1;
		this.myjetton=jetton;
		this.mymoney=money;
		if(linecount==0)
			this.myorder=-1;
		else 
			this.myorder=linecount;
		return linecount;
	}
	/**
	 * 盲注消息处理：记录大盲注金额
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	public void HandleBlind(BufferedReader reader,String head) throws IOException{
		String temp="";
		int linecount=0;
		while(!(temp=reader.readLine()).equals(head)){	
			String splittemp[]=temp.split(" ");
			int pid=new Integer(splittemp[0].substring(0, splittemp[0].length()-1)).intValue();
			int bb=new Integer(splittemp[1]).intValue();
			if(linecount==0){				
				desk.setBB(bb);//记录小盲注金额，当存在大盲时，BB更新为大盲值。无大盲时设为小盲值
				if(pid==mypid)//如果我是小盲，设置自己的当前的bet量为小盲值
					mybet=bb;
			}
			else if(linecount==1){//存在大盲时，linecount才会等于1				
				desk.setBB(bb);//记录大盲注金额
				if(pid==mypid)//如果我是大盲，设置自己的当前的bet量为大盲值
					mybet=bb;
			}
			++linecount;
		}
		
		//System.out.println("大盲金额："+desk.getBB());
	}
	/**
	 * 接收两张手牌
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	private void HanldeHoldCards(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";		
		while(!(temp=reader.readLine()).equals(head)){
			String splittemp[]=temp.split(" ");	
			Integer i=Card.NumeralSuit.get(splittemp[0]);
			int suitnumber=i.intValue();
			Card c=new Card(suitnumber, card2number(splittemp[1]));
			holdCards.add(c);
		}
		
		/*System.out.println("自己的手牌：");
		for(Card c:holdCards){
			System.out.println(c.getSuit()+" "+c.getNumber());
		}*/
	}
	/**
	 * 对发来的询问消息记录对手的动作，并且做出自己的动作
	 * @param reader
	 * @param head
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private void HanldeInquire(BufferedReader reader, String head) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		String temp="";
		int bet=0;
		StringBuffer curRoundAction=new StringBuffer();
		//保存本次Inquire传入的玩家pid、jetton、money bet action行。最多playercount行
		//除第一轮外，其他每轮都发全部玩家包括已弃牌玩家的动作
		String curRoundInquireMsg[]=new String[desk.playercount];
		int linecount=0;
		while(!(temp=reader.readLine()).equals(head)){
			String splittemp[]=temp.split(" ");
			int action_index=splittemp.length-1;
			if(!splittemp[0].equals("total")){
				String action=splittemp[action_index];//获取动作
				curRoundAction.append(action);//将已知动作加入buffer				
				curRoundInquireMsg[linecount]=temp;
				
				/*if(!action.equals("blind")){//对盲注信息不处理					
					Opponent opp=Pid_Opponent.get(pid);//获取相应对手对象
					if(action.equals("raise"))
						opp.raise_money=new Integer(splittemp[action_index-1]);//记录对手加注金额
					if(!opp.action.containsKey(desk.getcardStatus())){//未对该状态记录
						List<String> action_list=new ArrayList<String>();
						action_list.add(action);
						opp.action.put(desk.getcardStatus(), action_list);//将动作加入动作Map
					}
					else{
						List<String> action_list=opp.action.get(desk.getcardStatus());//获取对应列表
						action_list.add(action);//在列表中添加
					}
				}*/
			}
			else{
				desk.totalpot=new Integer(splittemp[action_index]);
			}
			linecount++;
		}
		
		/*System.out.println("当前总投注："+desk.totalpot);
		System.out.println("前面玩家最大bet："+bet);
		System.out.println("前面玩家行动消息："+curRoundAction.toString());
		System.out.println("向sever发送自己的action……");*/
		
		bet=getbet(curRoundInquireMsg);
		String myaction="noaction";
		if(!isDiscard){//没有弃牌才发决策消息给server
			if(desk.getcardStatus()==0){
				//System.out.println(mypid+" bet="+bet);
				preFlopAction pre=new preFlopAction(holdCards, myorder, bet, desk.getBB(), 
						desk.totalpot, desk.playercount, myjetton,inquirecount);
				myaction=pre.preFlopDecision();
				player2server.println(myaction);
			}
			else {
				//System.out.println(mypid+" bet="+bet+",轮数="+inquirecount);
				actionDecision mActionDecision=new actionDecision(holdCards, desk.sharedCards, bet, 
						desk.getBB(), getOpponentAction(curRoundAction.toString()), desk.totalpot, myjetton,inquirecount);
				myaction=mActionDecision.actionSendToServer();			
				player2server.println(myaction);
			}
			player2server.flush();
			
		}
		//System.out.println(mypid+"'s action="+myaction);
		inquirecount++;//增加轮数
		/*System.out.println("本轮个玩家bet_in:");
		for(Map.Entry<Integer, Opponent> entry:Pid_Opponent.entrySet()){//获取每个对手对象  		
			System.out.println(entry.getKey()+"-->"+entry.getValue().bet_in);			
		}*/
	}
	//获得bet并更新每个对手的bet_in
	private int getbet(String curRoundInquireMsg[]){//根据该pid对手的action决定bet——本轮前面玩家加入的最大筹码
		int result=0;
		int lastline=curRoundInquireMsg.length-1;
		String myMsg=curRoundInquireMsg[lastline];
		//获取非null的最后一行信息（curRoundInquireMsg长度是牌手数，当第一次询问时发来的牌手数小于desk.playercount）
		while(myMsg==null){
			lastline--;
			myMsg=curRoundInquireMsg[lastline];
		}
		String splitMsg[]=myMsg.split(" ");
		if(splitMsg[0].equals(mypid+"")){//如果是自己的信息
			mybet=new Integer(splitMsg[3]);//获取mybet
			myjetton=new Integer(splitMsg[1]);
			mymoney=new Integer(splitMsg[2]);//更新自己的筹码数
		}
		for(int i=0;i<curRoundInquireMsg.length&&curRoundInquireMsg[i]!=null;i++){
			splitMsg=curRoundInquireMsg[i].split(" ");
			if(splitMsg[4].equals("blind")||splitMsg[4].equals("call")
					||splitMsg[4].equals("raise")||splitMsg[4].equals("all_in")){
				result=new Integer(splitMsg[3])-mybet;
				//第一个call、raise或all_in的玩家总投入bet值减去自己已经投入的bet，即为我要跟注的最小筹码数
				break;
			}
			else if(splitMsg[4].equals("check")){
				result=0;
				break;
			}
			else{
				//fold
			}
		}
		
		return result<0?0:result;//如果小于0（对手all_in的bet值也小于自己的bet值），改为0
		
	}
	
	private String getOpponentAction(String curRoundAction ){
		if(curRoundAction.contains("all_in"))//包含all_in
			return "all_in";
		else if(curRoundAction.contains("raise"))//包含raise
			return "raise";
		else if(curRoundAction.contains("call"))//
			return "call";
		else if(curRoundAction.contains("check"))//
			return "check";
		else
			return "fold";
	}
	/**
	 * 记录公共牌
	 * @param reader2
	 * @param head
	 * @throws IOException
	 */
	private void HandleFlop(BufferedReader reader2, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		desk.setcardStatus(1);//牌局状态为flop
		inquirecount=1;//新的牌局状态，询问次数重置
		/*System.out.println("现有公共牌：");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	
	/**
	 * 记录Turn牌
	 * @param reader2
	 * @param head
	 * @throws IOException
	 */
	private void HandleTurn(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		desk.setcardStatus(2);
		inquirecount=1;//新的牌局状态，询问次数重置
		/*System.out.println("现有公共牌：");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	
	private void HandleRiver(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		desk.setcardStatus(3);
		inquirecount=1;//新的牌局状态，询问次数重置
	/*	System.out.println("现有公共牌：");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	/**
	 * 摊牌
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	private void HandleShowdown(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);
		}
		//System.out.println("shutdown消息体"+msgbody);
	}
	
	public void HandlePot_Win(BufferedReader reader,String head) throws IOException{
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
		}
		//System.out.println("pot-win消息体"+msgbody);
	}
	
	private int card2number(String str){
		int num;
		try{
			num=new Integer(str).intValue();
		}
		catch (IllegalArgumentException e){
			if(str.equals("A"))
				num=14;
			else if(str.equals("J"))
				num=11;
			else if(str.equals("Q"))
				num=12;
			else 
				num=13;
		}
		return num;
	}
}

