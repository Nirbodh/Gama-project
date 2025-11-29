const mongoose = require('mongoose');

const prizeSchema = new mongoose.Schema({
  match: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Match',
    required: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  amount: {
    type: Number,
    required: true,
    min: 0
  },
  position: {
    type: Number,
    required: true
  },
  distributedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  distributedAt: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Prize', prizeSchema);