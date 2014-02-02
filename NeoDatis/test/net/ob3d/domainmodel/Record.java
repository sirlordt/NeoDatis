package net.ob3d.domainmodel;

import java.io.Serializable;

public class Record implements Serializable {

	int recordType;
	int won;
	int lost;
	int kos;
	int draw;
	int winningStreak;
	String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Record(int type, String username) {
		super();
		setRecordType(type);
		setUsername(username);
		// TODO Auto-generated constructor stub
	}

	public int getWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}

	public int getLost() {
		return lost;
	}

	public void setLost(int lost) {
		this.lost = lost;
	}

	public int getKos() {
		return kos;
	}

	public void setKos(int kos) {
		this.kos = kos;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + won + "-" + lost + "-" + kos + "-" + draw + "]";
	}

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public int getTotalFights() {
		return won + lost + draw;
	}

	public int getWinningStreak() {
		return winningStreak;
	}

	public void setWinningStreak(int winningStreak) {
		this.winningStreak = winningStreak;
	}

}
