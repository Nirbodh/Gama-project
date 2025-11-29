const Prize = require('../models/Prize');
const Match = require('../models/Match');
const User = require('../models/User');

// @desc    Distribute prize (Admin only)
// @route   POST /api/prizes/distribute
// @access  Private/Admin
const distributePrize = async (req, res) => {
  try {
    const { matchId, winners } = req.body;

    const match = await Match.findById(matchId);
    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    // Create prize records and update winner wallets
    const prizeRecords = [];
    for (const winner of winners) {
      const user = await User.findById(winner.userId);
      if (user) {
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
      }
    }

    // Update match winner
    match.winner = winners[0].userId; // First position winner
    await match.save();

    res.json({ message: 'Prize distributed successfully', prizes: prizeRecords });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Get user prizes
// @route   GET /api/prizes/user
// @access  Private
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

module.exports = {
  distributePrize,
  getUserPrizes,
};