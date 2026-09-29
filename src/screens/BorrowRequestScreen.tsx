import React, { useState } from 'react';
import { HomeIndicator } from '../components/PhoneShell';
import { BackIcon, CalendarIcon, CheckIcon } from '../components/Icons';
import type { Item } from '../data';

interface Props {
  item: Item;
  onBack: () => void;
  onSuccess: () => void;
}

const durations = ['1 day', '2 days', '3 days', '5 days', '7 days'];

export const BorrowRequestScreen = ({ item, onBack, onSuccess }: Props) => {
  const [duration, setDuration] = useState('3 days');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);

  const returnDate = () => {
    const d = new Date();
    const days = parseInt(duration);
    d.setDate(d.getDate() + days);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  const handleSend = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setSent(true);
    }, 1500);
  };

  if (sent) {
    return (
      <div className="flex flex-col h-full items-center justify-center" style={{ background: '#F4F6FF' }}>
        <div className="flex flex-col items-center gap-6 px-8 text-center slide-up">
          {/* Success animation */}
          <div
            className="flex items-center justify-center"
            style={{
              width: 100,
              height: 100,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #3BB8A2 0%, #4F6AF0 100%)',
              boxShadow: '0 8px 32px rgba(59,184,162,0.35)',
            }}
          >
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <path d="M10 24L20 34L38 16" stroke="white" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" className="check-draw" />
            </svg>
          </div>
          <div>
            <h2 style={{ fontSize: 26, fontWeight: 800, color: '#1C2135', margin: 0 }}>Request Sent!</h2>
            <p style={{ fontSize: 15, color: '#5A607A', marginTop: 8, lineHeight: 1.6 }}>
              Waiting for {item.owner.split(' ')[0]} to respond. We'll notify you shortly.
            </p>
          </div>

          <div
            className="w-full p-4"
            style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA' }}
          >
            <div className="flex gap-3">
              <img src={item.image} alt={item.name} style={{ width: 60, height: 60, borderRadius: 12, objectFit: 'cover' }} />
              <div>
                <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135' }}>{item.name}</p>
                <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500 }}>Duration: {duration}</p>
                <p style={{ fontSize: 12, color: '#9BA3B8' }}>Return by: {returnDate()}</p>
              </div>
            </div>
          </div>

          <button
            onClick={onSuccess}
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
            View Activity
          </button>
          <button onClick={onBack} style={{ fontSize: 14, fontWeight: 600, color: '#9BA3B8' }}>
            Back to Home
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
        <button
          onClick={onBack}
          className="flex items-center justify-center flex-shrink-0"
          style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)' }}
        >
          <BackIcon size={20} color="white" />
        </button>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Borrow Request</h1>
          <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: 0 }}>Fill in the details below</p>
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5 pt-5 pb-4">
        {/* Item summary */}
        <div className="flex gap-3 p-4 mb-5" style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA' }}>
          <img src={item.image} alt={item.name} style={{ width: 72, height: 72, borderRadius: 14, objectFit: 'cover' }} />
          <div className="flex-1">
            <p style={{ fontSize: 15, fontWeight: 700, color: '#1C2135', margin: 0 }}>{item.name}</p>
            <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: '2px 0 6px' }}>{item.category}</p>
            <div className="flex items-center gap-2">
              <img src={item.ownerAvatar} alt={item.owner} style={{ width: 20, height: 20, borderRadius: '50%', objectFit: 'cover' }} />
              <span style={{ fontSize: 12, color: '#5A607A', fontWeight: 600 }}>{item.owner}</span>
            </div>
          </div>
        </div>

        {/* Duration */}
        <div className="mb-5">
          <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 10 }}>How long do you need it?</h3>
          <div className="flex flex-wrap gap-2">
            {durations.map(d => (
              <button
                key={d}
                onClick={() => setDuration(d)}
                style={{
                  height: 40,
                  paddingLeft: 16,
                  paddingRight: 16,
                  borderRadius: 12,
                  background: duration === d ? '#4F6AF0' : 'white',
                  border: `1.5px solid ${duration === d ? '#4F6AF0' : '#E5E8F2'}`,
                  fontSize: 13,
                  fontWeight: 700,
                  color: duration === d ? 'white' : '#5A607A',
                  transition: 'all 0.2s ease',
                  boxShadow: duration === d ? '0 4px 12px rgba(79,106,240,0.25)' : 'var(--bb-shadow-sm)',
                  cursor: 'pointer',
                }}
              >
                {d}
              </button>
            ))}
          </div>
        </div>

        {/* Return date */}
        <div
          className="flex items-center gap-3 p-4 mb-5"
          style={{ background: '#EEF1FF', borderRadius: 16, border: '1px solid rgba(79,106,240,0.15)' }}
        >
          <CalendarIcon size={20} color="#4F6AF0" />
          <div>
            <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: 0 }}>Return by</p>
            <p style={{ fontSize: 15, fontWeight: 700, color: '#1C2135', margin: 0 }}>{returnDate()}</p>
          </div>
        </div>

        {/* Message */}
        <div className="mb-5">
          <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 8 }}>Why do you need this? <span style={{ color: '#9BA3B8', fontWeight: 500 }}>(optional)</span></h3>
          <textarea
            value={message}
            onChange={e => setMessage(e.target.value)}
            placeholder="e.g. I need it for my upcoming physics exam..."
            rows={3}
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
              boxShadow: 'var(--bb-shadow-sm)',
              fontFamily: 'inherit',
            }}
          />
        </div>

        {/* Your info */}
        <div
          className="flex items-center gap-3 p-4"
          style={{ background: 'white', borderRadius: 16, border: '1px solid #F0F2FA' }}
        >
          <img
            src="https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format"
            alt="You"
            style={{ width: 44, height: 44, borderRadius: 14, objectFit: 'cover' }}
          />
          <div>
            <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>Nitya Kapoor</p>
            <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: 0 }}>BITS Pilani · ⭐ 4.9</p>
          </div>
        </div>
      </div>

      {/* Send button */}
      <div className="flex-shrink-0 px-5 pt-3 pb-2" style={{ background: 'white', borderTop: '1px solid #E5E8F2' }}>
        <button
          onClick={handleSend}
          className="w-full ripple"
          style={{
            height: 56,
            borderRadius: 18,
            background: loading ? '#9BA3B8' : 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
            color: 'white',
            fontSize: 16,
            fontWeight: 700,
            border: 'none',
            cursor: loading ? 'not-allowed' : 'pointer',
            boxShadow: loading ? 'none' : '0 8px 24px rgba(79,106,240,0.35)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 8,
          }}
        >
          {loading ? (
            <>
              <div style={{ width: 20, height: 20, border: '2px solid rgba(255,255,255,0.4)', borderTopColor: 'white', borderRadius: '50%', animation: 'spin 0.8s linear infinite' }} />
              Sending...
            </>
          ) : (
            'Send Request'
          )}
        </button>
      </div>
      <HomeIndicator />
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};
