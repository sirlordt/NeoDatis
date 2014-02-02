package net.ob3d.domainmodel;

import java.io.Serializable;

public class PlayerPresentation implements Serializable {

	Record record;
	PlayerInfo playerInfo;
	RatingEntry ratingEntry;

	public PlayerPresentation(Record record, PlayerInfo playerInfo, RatingEntry ratingEntry) {
		super();
		this.record = record;
		this.playerInfo = playerInfo;
		this.ratingEntry = ratingEntry;
	}

	public PlayerPresentation() {
		super();
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(PlayerInfo playerInfo) {
		this.playerInfo = playerInfo;
	}

	public RatingEntry getRatingEntry() {
		return ratingEntry;
	}

	public void setRatingEntry(RatingEntry ratingEntry) {
		this.ratingEntry = ratingEntry;
	}

	public String toString() {
		return ratingEntry.getPosition() + "\t" + ratingEntry.getRatingList() + "\t" + playerInfo.getName() + "\t" + record.toString();
	}
}
