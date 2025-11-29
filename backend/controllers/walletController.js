const Deposit = require('../models/Deposit');
const Withdrawal = require('../models/Withdrawal');
const User = require('../models/User');

// @desc    Request deposit
// @route   POST /api/wallet/deposit
// @access  Private
const requestDeposit = async (req, res) => {
  try {
    const { amount, transactionId } = req.body;

    const deposit = await Deposit.create({
      user: req.user._id,
      amount,
      transactionId
    });

    res.status(201).json(deposit);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Request withdrawal
// @route   POST /api/wallet/withdraw
// @access  Private
const requestWithdrawal = async (req, res) => {
  try {
    const { amount, transactionId } = req.body;
    const user = await User.findById(req.user._id);

    if (user.walletBalance < amount) {
      return res.status(400).json({ message: 'Insufficient balance' });
    }

    const withdrawal = await Withdrawal.create({
      user: req.user._id,
      amount,
      transactionId
    });

    res.status(201).json(withdrawal);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Approve deposit (Admin only)
// @route   PUT /api/wallet/deposit/:id/approve
// @access  Private/Admin
const approveDeposit = async (req, res) => {
  try {
    const deposit = await Deposit.findById(req.params.id).populate('user');

    if (!deposit) {
      return res.status(404).json({ message: 'Deposit not found' });
    }

    deposit.status = 'approved';
    deposit.approvedBy = req.user._id;
    deposit.approvedAt = new Date();

    // Update user wallet
    deposit.user.walletBalance += deposit.amount;
    await deposit.user.save();
    await deposit.save();

    res.json({ message: 'Deposit approved successfully', deposit });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Approve withdrawal (Admin only)
// @route   PUT /api/wallet/withdrawal/:id/approve
// @access  Private/Admin
const approveWithdrawal = async (req, res) => {
  try {
    const withdrawal = await Withdrawal.findById(req.params.id).populate('user');

    if (!withdrawal) {
      return res.status(404).json({ message: 'Withdrawal not found' });
    }

    withdrawal.status = 'approved';
    withdrawal.approvedBy = req.user._id;
    withdrawal.approvedAt = new Date();

    // Update user wallet (amount already deducted when requested)
    await withdrawal.save();

    res.json({ message: 'Withdrawal approved successfully', withdrawal });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// @desc    Get user transactions
// @route   GET /api/wallet/transactions
// @access  Private
const getUserTransactions = async (req, res) => {
  try {
    const deposits = await Deposit.find({ user: req.user._id }).sort({ createdAt: -1 });
    const withdrawals = await Withdrawal.find({ user: req.user._id }).sort({ createdAt: -1 });

    res.json({
      deposits,
      withdrawals
    });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = {
  requestDeposit,
  requestWithdrawal,
  approveDeposit,
  approveWithdrawal,
  getUserTransactions,
};