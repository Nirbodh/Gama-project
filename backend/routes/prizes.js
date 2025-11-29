const express = require('express');
const {
  distributePrize,
  getUserPrizes,
} = require('../controllers/prizeController');
const { protect, admin } = require('../middleware/auth');

const router = express.Router();

router.post('/distribute', protect, admin, distributePrize);
router.get('/user', protect, getUserPrizes);

module.exports = router;