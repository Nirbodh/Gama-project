const Match = require('../models/Match');
const User = require('../models/User');

// @desc    Create a match
// @route   POST /api/matches
// @access  Private
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

    // For normal matches, check if user has sufficient balance for entry fee
    if (type === 'normal') {
      const user = await User.findById(req.user._id);
      if (user.walletBalance < entryFee) {
        return res.status(400).json({ message: 'Insufficient balance to create match' });
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

// @desc    Get all matches
// @route   GET /api/matches
// @access  Public
const getMatches = async (req, res) => {
  try {
    const { status, type } = req.query;
    let filter = {};

    if (status) filter.status = status;
    if (type) filter.type = type;

    // Non-admin users only see approved matches
    if (!req.user || req.user.role !== 'admin') {
      filter.status = 'approved';
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

// @desc    Join a match
// @route   POST /api/matches/:id/join
// @access  Private
const joinMatch = async (req, res) => {
  try {
    const match = await Match.findById(req.params.id);
    const user = await User.findById(req.user._id);

    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    if (match.status !== 'approved') {
      return res.status(400).json({ message: 'Match is not available for joining' });
    }

    if (match.currentParticipants >= match.maxParticipants) {
      return res.status(400).json({ message: 'Match is full' });
    }

    // Check if user already joined
    const alreadyJoined = match.participants.some(
      participant => participant.user.toString() === req.user._id.toString()
    );

    if (alreadyJoined) {
      return res.status(400).json({ message: 'Already joined this match' });
    }

    // Check wallet balance
    if (user.walletBalance < match.entryFee) {
      return res.status(400).json({ message: 'Insufficient balance' });
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

// @desc    Submit match result
// @route   POST /api/matches/:id/submit-result
// @access  Private
const submitResult = async (req, res) => {
  try {
    const { screenshot } = req.body;
    const match = await Match.findById(req.params.id);

    if (!match) {
      return res.status(404).json({ message: 'Match not found' });
    }

    // Check if user is participant
    const isParticipant = match.participants.some(
      participant => participant.user.toString() === req.user._id.toString()
    );

    if (!isParticipant) {
      return res.status(400).json({ message: 'Not a participant of this match' });
    }

    match.screenshot = screenshot;
    match.status = 'finished';
    await match.save();

    res.json({ message: 'Result submitted successfully', match });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Approve match (Admin only)
// @route   PUT /api/matches/:id/approve
// @access  Private/Admin
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
  approveMatch,
};