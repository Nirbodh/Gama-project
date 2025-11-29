const express = require('express');
const {
  createMatch,
  getMatches,
  joinMatch,
  submitResult,
  approveMatch,
} = require('../controllers/matchController');
const { protect, admin } = require('../middleware/auth');

const router = express.Router();

router.route('/')
  .get(protect, getMatches)
  .post(protect, createMatch);

router.post('/:id/join', protect, joinMatch);
router.post('/:id/submit-result', protect, submitResult);
router.put('/:id/approve', protect, admin, approveMatch);

module.exports = router;