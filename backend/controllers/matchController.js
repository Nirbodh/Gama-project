const Match = require('../models/Match');
const User = require('../models/User');

// ===============================
// CREATE MATCH
// ===============================
const createMatch = async (req, res) => {
  try {
    const {
      title,
      description,
      type,
      gameType,
      entryFee,
      prizePool,
      maxParticipants,
      startTime,
      rules
    } = req.body;

    // Prize pool validation only if entry fee > 0
    if (entryFee > 0) {
      const expectedEarnings = entryFee * maxParticipants;

      if (prizePool > expectedEarnings * 0.9) {
        return res.status(400).json({
          message: 'Prize pool too high for the entry fee and participants'
        });
      }
    }

    const match = await Match.create({
      title,
      description,
      type,
      gameType,
      entryFee,
      prizePool,
      maxParticipants,
      startTime,
      rules,
      createdBy: req.user._id,
      status: req.user.role === 'admin' ? 'approved' : 'pending'
    });

    res.status(201).json(match);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};


// ===============================
// GET MATCHES
// ===============================
const getMatches = async (req, res) => {
  try {
    const { status, type } = req.query;
    let filter = {};

    if (status) filter.status = status;
    if (type) filter.type = type;

    if (!req.user || req.user.role !== 'admin') {
      filter.$or = [
        { status: 'approved' },
        { createdBy: req.user?._id } // user should see their own pending matches
      ];
    }

    const matches = await Match.find(filter)
      .populate('createdBy', 'name email')
      .populate('participants.user', 'name')
      .populate('winner', 'name')
      .sort({ createdAt: -1 });

    res.json(matches);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};


// ===============================
// JOIN MATCH
// ===============================
const joinMatch = async (req, res) => {
  try {
    const match = await Match.findById(req.params.id);
    const user = await User.findById(req.user._id);

    if (!match) return res.status(404).json({ message: 'Match not found' });

    if (match.status !== 'approved') {
      return res.status(400).json({ message: 'Match is not available for joining' });
    }

    if (match.currentParticipants >= match.maxParticipants) {
      return res.status(400).json({ message: 'Match is full' });
    }

    const alreadyJoined = match.participants.some(
      p => p.user.toString() === req.user._id.toString()
    );
    if (alreadyJoined) {
      return res.status(400).json({ message: 'Already joined this match' });
    }

    if (match.createdBy.toString() === req.user._id.toString()) {
      return res.status(400).json({ message: 'Cannot join your own match' });
    }

    if (user.walletBalance < match.entryFee) {
      return res.status(400).json({ message: 'Insufficient balance to join match' });
    }

    // Deduct entry fee
    user.walletBalance -= match.entryFee;
    await user.save();

    // Add participant
    match.participants.push({ user: req.user._id });
    match.currentParticipants += 1;

    await match.save();

    res.json({ message: 'Successfully joined the match', match });

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};


// ===============================
// SUBMIT RESULT
// ===============================
const submitResult = async (req, res) => {
  try {
    const { screenshot } = req.body;
    const match = await Match.findById(req.params.id);

    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    const isParticipant = match.participants.some(
      p => p.user.toString() === req.user._id.toString()
    );

    if (!isParticipant) {
      return res.status(400).json({ message: 'Not a participant of this match' });
    }

    match.screenshot = screenshot;
    match.submittedBy = req.user._id;
    match.status = 'result-submitted'; // IMPORTANT CHANGE

    await match.save();

    res.json({ message: 'Result submitted successfully', match });

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};


// ===============================
// APPROVE MATCH (ADMIN)
// ===============================
const approveMatch = async (req, res) => {
  try {
    const match = await Match.findById(req.params.id);

    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    match.status = 'approved';
    await match.save();

    res.json({ message: 'Match approved successfully', match });

  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};


module.exports = {
  createMatch,
  getMatches,
  joinMatch,
  submitResult,
  approveMatch
};
