const express = require('express');
const {
  requestDeposit,
  requestWithdrawal,
  approveDeposit,
  approveWithdrawal,
  getUserTransactions,
} = require('../controllers/walletController');
const { protect, admin } = require('../middleware/auth');

const router = express.Router();

router.post('/deposit', protect, requestDeposit);
router.post('/withdraw', protect, requestWithdrawal);
router.get('/transactions', protect, getUserTransactions);
router.put('/deposit/:id/approve', protect, admin, approveDeposit);
router.put('/withdrawal/:id/approve', protect, admin, approveWithdrawal);

module.exports = router;