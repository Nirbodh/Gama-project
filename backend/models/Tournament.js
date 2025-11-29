const mongoose = require('mongoose');

const tournamentSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    required: true
  },
  entryFee: {
    type: Number,
    required: true,
    min: 0
  },
  totalPrize: {
    type: Number,
    required: true,
    min: 0
  },
  maxTeams: {
    type: Number,
    required: true
  },
  currentTeams: {
    type: Number,
    default: 0
  },
  status: {
    type: String,
    enum: ['upcoming', 'ongoing', 'completed', 'cancelled'],
    default: 'upcoming'
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  startDate: {
    type: Date,
    required: true
  },
  endDate: {
    type: Date,
    required: true
  },
  matches: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Match'
  }],
  winner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Tournament', tournamentSchema);