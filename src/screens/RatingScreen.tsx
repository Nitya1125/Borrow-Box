import React, { useState } from 'react';
import { HomeIndicator } from '../components/PhoneShell';
import { BackIcon, StarIcon } from '../components/Icons';

interface Props {
  onBack: () => void;
  onDone: () => void;
}

export const RatingScreen = ({ onBack, onDone }: Props) => {
  const [rating, setRating] = useState(0);
  const [hovered, setHovered] = useState(0);
  const [review, setReview] = useState('');
  const [submitted, setSubmitted] = useState(false);

  const labels = ['', 'Terrible', 'Poor', 'Okay', 'Great', 'Excellent!'];
  const effective = hovered || rating;

  if (submitted) {
    return (
      <div className="flex flex-col h-full items-center justify-center" style={{ background: '#F4F6FF' }}>
        <div className="flex flex-col items-center gap-6 px-8 text-center slide-up">
          <div style={{ fontSize: 80 }}>🌟</div>
          <div>
            <h2 style={{ fontSize: 26, fontWeight: 800, color: '#1C2135', margin: 0 }}>Thank you!</h2>
            <p style={{ fontSize: 15, color: '#5A607A', marginTop: 8, lineHeight: 1.6 }}>
              Your review helps build trust in the BorrowBox community.
            </p>
          </div>
          <button
            onClick={onDone}
            style={{
              width: '100%',
              height: 56,
              borderRadius: 18,
              background: 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              color: 'white',
              fontSize: 16,
              fontWeight: 700,
              border: 'none',
              cursor: 'pointer',
              boxShadow: '0 8px 24px rgba(79,106,240,0.35)',
            }}
          >
            Done
          </button>
        </div>
        <HomeIndicator />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      {/* Header */}
      <div
        className="flex items-center gap-4 px-5 pt-12 pb-5"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }}>
          <BackIcon size={20} color="white" />
        </button>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Rate Experience</h1>
          <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: 0 }}>Borrowing completed!</p>
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5 pt-6 pb-4">
        {/* Item returned */}
        <div
          className="flex gap-3 p-4 mb-6"
          style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA' }}
        >
          <img
            src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format"
            alt="Item"
            style={{ width: 64, height: 64, borderRadius: 14, objectFit: 'cover' }}
          />
          <div>
            <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>Data Structures — Cormen</p>
            <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: '2px 0 6px' }}>Borrowed from Priya Sharma</p>
            <span style={{ fontSize: 11, fontWeight: 700, color: '#3BB8A2', background: '#E6F7F4', borderRadius: 6, padding: '2px 8px' }}>
              ✓ Returned Successfully
            </span>
          </div>
        </div>

        {/* Rating prompt */}
        <div className="flex flex-col items-center gap-5 mb-6">
          <div>
            <h2 style={{ fontSize: 22, fontWeight: 800, color: '#1C2135', textAlign: 'center', margin: '0 0 4px' }}>
              How was your experience?
            </h2>
            <p style={{ fontSize: 14, color: '#9BA3B8', textAlign: 'center', margin: 0 }}>Rate your borrowing experience</p>
          </div>

          {/* Stars */}
          <div className="flex gap-3">
            {[1, 2, 3, 4, 5].map(i => (
              <button
                key={i}
                onMouseEnter={() => setHovered(i)}
                onMouseLeave={() => setHovered(0)}
                onClick={() => setRating(i)}
                style={{
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  padding: 4,
                  transition: 'transform 0.15s ease',
                  transform: i === effective ? 'scale(1.2)' : 'scale(1)',
                }}
              >
                <svg width="42" height="42" viewBox="0 0 24 24" fill={i <= effective ? '#F5C842' : 'none'}>
                  <path
                    d="M12 2L14.85 9.3L22.5 9.3L16.5 13.9L18.85 21.1L12 16.5L5.15 21.1L7.5 13.9L1.5 9.3L9.15 9.3L12 2Z"
                    stroke="#F5C842"
                    strokeWidth="1.5"
                    strokeLinejoin="round"
                  />
                </svg>
              </button>
            ))}
          </div>

          {effective > 0 && (
            <span
              style={{
                fontSize: 18,
                fontWeight: 800,
                color: '#F5C842',
                animation: 'staggerIn 0.2s ease both',
              }}
            >
              {labels[effective]}
            </span>
          )}
        </div>

        {/* Review text */}
        <div className="mb-6">
          <label style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 8, display: 'block' }}>
            Write a review <span style={{ color: '#9BA3B8', fontWeight: 500 }}>(optional)</span>
          </label>
          <textarea
            value={review}
            onChange={e => setReview(e.target.value)}
            placeholder="Share your experience with the community..."
            rows={4}
            style={{
              width: '100%',
              borderRadius: 14,
              border: '1.5px solid #E5E8F2',
              background: 'white',
              padding: 14,
              fontSize: 14,
              fontWeight: 500,
              color: '#1C2135',
              resize: 'none',
              fontFamily: 'inherit',
              boxShadow: 'var(--bb-shadow-sm)',
            }}
          />
        </div>

        {/* Quick tags */}
        <div className="mb-6">
          <p style={{ fontSize: 13, fontWeight: 600, color: '#5A607A', marginBottom: 8 }}>Quick tags:</p>
          <div className="flex flex-wrap gap-2">
            {['Good condition', 'Punctual', 'Friendly', 'Accurate description', 'Would borrow again'].map(tag => (
              <button
                key={tag}
                style={{
                  padding: '6px 14px',
                  borderRadius: 10,
                  background: 'white',
                  border: '1.5px solid #E5E8F2',
                  fontSize: 12,
                  fontWeight: 600,
                  color: '#5A607A',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                }}
                onClick={e => {
                  const el = e.currentTarget;
                  if (el.style.background === 'rgb(238, 241, 255)') {
                    el.style.background = 'white';
                    el.style.borderColor = '#E5E8F2';
                    el.style.color = '#5A607A';
                  } else {
                    el.style.background = '#EEF1FF';
                    el.style.borderColor = '#4F6AF0';
                    el.style.color = '#4F6AF0';
                  }
                }}
              >
                {tag}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={() => { if (rating > 0) setSubmitted(true); }}
          style={{
            width: '100%',
            height: 56,
            borderRadius: 18,
            background: rating === 0 ? '#E5E8F2' : 'linear-gradient(135deg, #F5C842 0%, #F0A830 100%)',
            color: rating === 0 ? '#9BA3B8' : 'white',
            fontSize: 16,
            fontWeight: 700,
            border: 'none',
            cursor: rating === 0 ? 'not-allowed' : 'pointer',
            boxShadow: rating > 0 ? '0 8px 24px rgba(245,200,66,0.35)' : 'none',
            transition: 'all 0.3s ease',
          }}
        >
          Submit Review
        </button>
      </div>
      <HomeIndicator />
    </div>
  );
};
