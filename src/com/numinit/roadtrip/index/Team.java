package com.numinit.roadtrip.index;

public class Team {
	public static final String[] TEAMS = {"NEU", "RES", "ENL"};
	public static final int NEU = 0, RES = 1, ENL = 2;
	public static final Team NEU_TEAM = new Team(NEU), RES_TEAM = new Team(RES), ENL_TEAM = new Team(ENL);

	/**
	 * The team
	 */
	private int _team;
	
	/**
	 * Initializes this Team
	 * @param team The team
	 */
	public Team(int team) {
		this._team = team;
	}
	
	@Override
	public String toString() {
		return TEAMS[this.integer()];
	}
	
	/**
	 * Converts this team to a console string
	 * @return The console string
	 */
	public String toConsole() {
		switch(this.integer()) {
		case NEU:
			return String.format("\u001b[0;37;49m%s\u001b[0m", this.toString());
		case RES:
			return String.format("\u001b[0;34;49m%s\u001b[0m", this.toString());
		case ENL:
			return String.format("\u001b[0;32;49m%s\u001b[0m", this.toString());
		default:
			throw new IllegalStateException("invalid team");
		}
	}
	
	/**
	 * Returns the team
	 * @return The team
	 */
	public int integer() {
		return this._team;
	}
	
	/**
	 * Returns an interned team for the specified team
	 * @param team The team name
	 * @return The team
	 */
	public static final Team getTeamFor(String team) {
		if (team.equals("NEUTRAL")) {
			return NEU_TEAM;
		} else if (team.equals("RESISTANCE")) {
			return RES_TEAM;
		} else if (team.equals("ENLIGHTENED") || team.equals("ALIENS")) {
			return ENL_TEAM;
		} else {
			throw new IllegalArgumentException(String.format("invalid team `%s'", team));
		}
	}
}
