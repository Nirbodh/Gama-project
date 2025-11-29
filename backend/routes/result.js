const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const Match = require('../models/Match');
const User = require('../models/User');

// Submit match result (winner screenshot)
router.post('/:matchId/result', auth, async (req, res) => {
    try {
        const { winnerId } = req.body; // winner's userId
        const match = await Match.findById(req.params.matchId);
        if(!match) return res.status(404).json({ message: "Match not found" });
        if(match.status !== "started") return res.status(400).json({ message: "Match not started or already finished" });

        // update match
        match.status = "finished";
        match.winner = winnerId;
        await match.save();

        // credit prize
        const winner = await User.findById(winnerId);
        winner.wallet += match.prize;
        await winner.save();

        res.json({ message: "Prize distributed", winnerWallet: winner.wallet });
    } catch(err) {
        res.status(500).json({ message: "Server error" });
    }
});

module.exports = router;
