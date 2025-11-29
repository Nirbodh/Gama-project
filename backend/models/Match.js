const mongoose = require('mongoose');

const matchSchema = new mongoose.Schema({
  title: {
    type: String,
    required: [true, 'Please add a match title'],
    trim: true
  },
  description: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['normal', 'tournament'],
    required: true
  },
  gameType: {
    type: String,
    required: true,
    trim: true
  },
  entryFee: {
    type: Number,
    required: true,
    min: 0
  },
  prizePool: {
    type: Number,
    required: true,
    min: 0
  },
  maxParticipants: {
    type: Number,
    required: true,
    min: 2
  },
  currentParticipants: {
    type: Number,
    default: 0
  },
  status: {
    type: String,
    enum: ['pending', 'approved', 'started', 'finished', 'cancelled'],
    default: 'pending'
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  participants: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    joinedAt: {
      type: Date,
      default: Date.now
    }
  }],
  startTime: {
    type: Date,
    required: true
  },
  endTime: {
    type: Date
  },
  winner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  screenshot: {
    type: String // URL for result screenshot
  },
  rules: [{
    type: String
  }]
}, {
  timestamps: true
});

module.exports = mongoose.model('Match', matchSchema);