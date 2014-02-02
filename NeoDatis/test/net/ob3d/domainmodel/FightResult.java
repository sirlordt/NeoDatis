package net.ob3d.domainmodel;

import java.io.Serializable;
import java.util.Date;

public class FightResult implements Serializable {
	ScoreCard scoreCardJudge1;
	ScoreCard scoreCardJudge2;
	ScoreCard scoreCardJudge3;
	String resultId;
	Date date;
	int fightType;
	public static final String[] type_descriptions = { "Draw", "Majority draw", "Split decision", "Unanimous decision", "Ko", "Tko" };
	public static final int type_tko = 5;
	public static final int type_ko = 4;
	public static final int type_unanimous_decision = 3;
	public static final int type_split_decision = 2;
	public static final int type_majority_draw = 1;
	public static final int type_draw = 0;
	int scheduledFor;
	int wonInRound;
	int type;
	String winner = "";
	String username1;
	String username2;
	String name1;
	String name2;
	int careerLevel;

	public FightResult() {
		super();
	}

	public static String getTypeDescription(int type) {
		return type_descriptions[type];
	}

	public String getUsername1() {
		return username1;
	}

	public void setUsername1(String player1) {
		this.username1 = player1;
	}

	public String getUsername2() {
		return username2;
	}

	public void setUsername2(String player2) {
		this.username2 = player2;
	}

	public ScoreCard getScoreCardJudge1() {
		return scoreCardJudge1;
	}

	public void setScoreCardJudge1(ScoreCard scoreCard1) {
		this.scoreCardJudge1 = scoreCard1;
	}

	public ScoreCard getScoreCardJudge2() {
		return scoreCardJudge2;
	}

	public void setScoreCardJudge2(ScoreCard scoreCard2) {
		this.scoreCardJudge2 = scoreCard2;
	}

	public ScoreCard getScoreCardJudge3() {
		return scoreCardJudge3;
	}

	public void setScoreCardJudge3(ScoreCard scoreCard3) {
		this.scoreCardJudge3 = scoreCard3;
	}

	public int getScheduledFor() {
		return scheduledFor;
	}

	public void setScheduledFor(int scheduledFor) {
		this.scheduledFor = scheduledFor;
	}

	public int getWonInRound() {
		return wonInRound;
	}

	public void setWonInRound(int wonInRound) {
		this.wonInRound = wonInRound;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getFightType() {
		return fightType;
	}

	public void setFightType(int fightType) {
		this.fightType = fightType;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getCareerLevel() {
		return careerLevel;
	}

	public void setCareerLevel(int careerLevel) {
		this.careerLevel = careerLevel;
	}
}
