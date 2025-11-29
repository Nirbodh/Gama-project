const Prize = require('../models/Prize');
const Match = require('../models/Match');
const User = require('../models/User');


// ===============================
//  MANUAL PRIZE DISTRIBUTE
// ===============================
const distributePrize = async (req, res) => {
  try {
    const { matchId, winners } = req.body;

    const match = await Match.findById(matchId);
    if (!match) return res.status(404).json({ message: 'Match not found' });

    if (!Array.isArray(winners) || winners.length === 0) {
      return res.status(400).json({ message: 'No winners provided' });
    }

    const prizeRecords = [];

    // Process all winners in parallel
    await Promise.all(
      winners.map(async (winner) => {
        const user = await User.findById(winner.userId);
        if (!user) return;

        user.walletBalance += winner.amount;
        await user.save();

        const prize = await Prize.create({
          match: matchId,
          user: winner.userId,
          amount: winner.amount,
          position: winner.position,
          distributedBy: req.user._id
        });

        prizeRecords.push(prize);
      })
    );

    // Set match winner = first position
    match.winner = winners[0].userId;
    await match.save();

    res.json({
      message: 'Prize distributed successfully',
      prizes: prizeRecords
    });

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};



// ===============================
//  AUTO PRIZE DISTRIBUTION
// ===============================
const autoDistributePrizes = async (req, res) => {
  try {
    const match = await Match.findById(req.params.matchId)
      .populate('participants.user');

    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    // Total Collection = entryFee * participants count
    const totalCollection = match.entryFee * match.participants.length;

    // Prize Breakdown (customizable)
    const prizeBreakdown = {
      first: totalCollection * 0.6,
      second: totalCollection * 0.3,
      third: totalCollection * 0.1
    };

    const winners = [];

    // For now, only first place
    if (match.participants.length > 0) {
      const winner = match.participants[0].user;

      winner.walletBalance += prizeBreakdown.first;
      await winner.save();

      await Prize.create({
        match: match._id,
        user: winner._id,
        amount: prizeBreakdown.first,
        position: 1,
        distributedBy: req.user._id
      });

      winners.push({
        userId: winner._id,
        position: 1,
        amount: prizeBreakdown.first
      });
    }

    // Update match status
    match.status = 'finished';
    match.winner = match.participants[0]?.user._id || null;
    await match.save();

    res.json({
      message: 'Prizes distributed successfully',
      totalCollection,
      prizeBreakdown,
      winners
    });

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};



// ===============================
//  GET USER PRIZES
// ===============================
const getUserPrizes = async (req, res) => {
  try {
    const prizes = await Prize.find({ user: req.user._id })
      .populate('match', 'title gameType')
      .sort({ distributedAt: -1 });

    res.json(prizes);

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};



// EXPORT CONTROLLER
module.exports = {
  distributePrize,
  autoDistributePrizes,
  getUserPrizes
};
