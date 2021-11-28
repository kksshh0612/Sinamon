package Chatting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


class ChatHandlerObject extends Thread //처리해주는 곳(소켓에 대한 정보가 담겨있는 곳. 소켓을 처리함)

{
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	public int a=0;
	private Socket socket;
	//private InfoDTO dto;
	///private Info command;
	private List <ChatHandlerObject> userlist;
	private Room room;
	private int roomid;
	private ChatHandlerObject handler;
	
	//생성자

	public ChatHandlerObject(Socket socket) throws IOException {
		
		this.socket = socket;
		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
		
		//순서가 뒤바뀌면 값을 입력받지 못하는 상황이 벌어지기 때문에 반드시 writer부터 생성시켜주어야 함!!!!!!

		
	}
	public ChatHandlerObject(ChatHandlerObject handler) {
		this.handler=handler;
	}
	
	public Socket getsocket() {
		return socket;
	}
	public ObjectOutputStream getwriter() {
		return writer;
	}
	public ObjectInputStream getreader() {
		return reader;
	}
	public int getroomid() {
		return roomid;
	}
	public void run(){
		InfoDTO dto = null;
		String nickName;
		try{
			int loopcount=0;
			while(true){
				System.out.println("handler2'a in handler"+a);
				System.out.println("handlerroomid"+RoomManager.getroomid());
	//			dto=(InfoDTO)reader.readObject();
				reader=handler.getreader();
				dto=(InfoDTO)reader.readObject();
				System.out.println("여기다ㅣ"+dto.getroomid());
				if(loopcount==0) {	//루프첫바퀴때만 방생성및 handler추가해줌
				roomid=dto.getroomid();
				Room eachroom=RoomManager.getroom(roomid);		//방번호를통해 RoomManager에서방을 받아와서
				eachroom.setuser(handler);	//방에 handler추가
				}
				
				
				nickName=dto.getNickName();
				int roomnumber=dto.getroomid();
				
				
				//if(userlist.size()==2) {
					//System.out.println("제발돼라!!");
				//}

				//System.out.println("배열 크기:"+ar.length);
				//사용자가 접속을 끊었을 경우. 프로그램을 끝내서는 안되고 남은 사용자들에게 퇴장메세지를 보내줘야 한다. 
				if(dto.getCommand()==Info.EXIT){ 
			
					InfoDTO sendDto = new InfoDTO();
					//나가려고 exit를 보낸 클라이언트에게 답변 보내기

					sendDto.setCommand(Info.EXIT);
					writer.writeObject(sendDto);
					writer.flush();

					reader.close();
					writer.close();
					socket.close();

					//room.deleteuser();
					userlist.remove(this);

					sendDto.setCommand(Info.NOTICE);
					//sendDto.setWhossend(who.server);
					sendDto.setMessage(nickName+"님이 퇴장하였습니다.");
					broadcast(sendDto);
					break;
				} else if(dto.getCommand()==Info.JOIN){
					//��� ����ڿ��� �޼��� ������
					//nickName = dto.getNickName();
					//��� Ŭ���̾�Ʈ���� ���� �޼��� ������ ��
					InfoDTO sendDto = new InfoDTO();
					sendDto.setCommand(Info.NOTICE);
					//sendDto.setWhossend(who.server);
					sendDto.setMessage(nickName+"님이 입장하였습니다.");
					sendDto.sethandlerroomnumber(dto.getroomid());
					broadcast(sendDto);
				} else if(dto.getCommand()==Info.SEND){
					System.out.println("내가속한 룸:"+roomid);
					InfoDTO sendDto = new InfoDTO();
					sendDto.setCommand(Info.SEND);
					sendDto.setMessage("["+nickName+"] "+ dto.getMessage());
					broadcast(sendDto);
				}
				loopcount++;
			}//while

		} catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	
		
	}
	
	

	//다른 클라이언트에게 전체 메세지 보내주기
	public void broadcast(InfoDTO sendDto) throws IOException {
		System.out.println("음?"+roomid);
		Room eachroom=RoomManager.getroom(roomid);		//방번호를통해 RoomManager에서방을 받아와서
		for(ChatHandlerObject handler : eachroom.getuser()) {
			System.out.println("handler's room="+handler.getroomid());
			handler.writer.writeObject(sendDto); //핸들러 안의 writer에 값을 보내기
			handler.writer.flush();  //핸들러 안의 writer 값 비워주기
		}
	}
	
	
	/*public void broadcast(InfoDTO sendDto) throws IOException {
		
		if(userlist==null) {
			System.out.println("userlist는 null입니다");
		}
		
		
		for(ChatHandlerObject handler : userlist) {
			handler.writer.writeObject(sendDto); //핸들러 안의 writer에 값을 보내기
			handler.writer.flush();  //핸들러 안의 writer 값 비워주기
		}
	}*/
	
}

