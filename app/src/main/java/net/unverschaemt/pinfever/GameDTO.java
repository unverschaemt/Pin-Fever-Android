package net.unverschaemt.pinfever;

import java.util.List;

/**
 * Created by kkoile on 12.05.15.
 */
public class GameDTO {
    public long id;
    public int state;
    public long activeRound;
    public List<RoundDTO> rounds;
    public List<ParticipantDTO> participants;
}
