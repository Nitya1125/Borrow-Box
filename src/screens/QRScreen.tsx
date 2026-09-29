import React, { useState } from 'react';
import { HomeIndicator } from '../components/PhoneShell';
import { BackIcon, CheckIcon } from '../components/Icons';
import type { Item } from '../data';

interface Props {
  item: Item;
  onBack: () => void;
}

// Simple visual QR-like grid pattern
const QRPattern = () => {
  const pattern = [
    [1,1,1,1,1,1,1,0,1,0,0,1,0,0,1,1,1,1,1,1,1],
    [1,0,0,0,0,0,1,0,1,1,0,0,1,0,1,0,0,0,0,0,1],
    [1,0,1,1,1,0,1,0,0,0,1,1,0,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,0,1],
    [1,0,0,0,0,0,1,0,1,0,1,0,1,0,1,0,0,0,0,0,1],
    [1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1],
    [0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0],
    [1,1,0,1,1,0,1,1,0,0,1,0,1,1,0,1,0,1,1,0,1],
    [0,1,0,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0],
    [1,0,1,1,0,1,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1],
    [0,1,0,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0],
    [1,0,1,1,0,1,1,0,1,0,1,0,1,1,0,1,0,1,1,0,1],
    [0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0],
    [1,1,1,1,1,1,1,0,0,1,0,1,0,0,1,1,1,1,1,1,1],
    [1,0,0,0,0,0,1,0,1,0,1,0,1,0,1,0,0,0,0,0,1],
    [1,0,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1],
    [1,0,1,1,1,0,1,0,0,1,0,1,0,0,1,0,1,1,1,0,1],
    [1,0,0,0,0,0,1,0,1,0,1,0,1,0,1,0,0,0,0,0,1],
    [1,1,1,1,1,1,1,0,0,1,0,1,0,0,1,1,1,1,1,1,1],
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: `repeat(21, 1fr)`, gap: 1, padding: 12, background: 'white', borderRadius: 16 }}>
      {pattern.flat().map((cell, i) => (
        <div key={i} style={{ width: 10, height: 10, background: cell ? '#1C2135' : 'white', borderRadius: 1 }} />
      ))}
    </div>
  );
};

export const QRScreen = ({ item, onBack }: Props) => {
  const [returned, setReturned] = useState(false);

  if (returned) {
    return (
      <div className="flex flex-col h-full items-center justify-center" style={{ background: '#F4F6FF' }}>
        <div className="flex flex-col items-center gap-6 px-8 text-center slide-up">
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
            <h2 style={{ fontSize: 26, fontWeight: 800, color: '#1C2135', margin: 0 }}>Item Returned!</h2>
            <p style={{ fontSize: 15, color: '#5A607A', marginTop: 8, lineHeight: 1.6 }}>
              Thank you for returning on time. Please leave a review!
            </p>
          </div>
          <button
            onClick={onBack}
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
            Rate Your Experience
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
        <button onClick={onBack} className="flex items-center justify-center" style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)' }}>
          <BackIcon size={20} color="white" />
        </button>
        <div>
          <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Ready to Borrow</h1>
          <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: 0 }}>Show this QR to the owner</p>
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5 pt-6 pb-4">
        {/* Item info */}
        <div className="flex gap-3 p-4 mb-6" style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA' }}>
          <img src={item.image} alt={item.name} style={{ width: 56, height: 56, borderRadius: 12, objectFit: 'cover' }} />
          <div>
            <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>{item.name}</p>
            <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: '2px 0' }}>{item.owner}</p>
            <span style={{ fontSize: 11, fontWeight: 700, color: '#3BB8A2', background: '#E6F7F4', borderRadius: 6, padding: '2px 8px' }}>
              ✓ Approved
            </span>
          </div>
        </div>

        {/* QR code */}
        <div
          className="flex flex-col items-center p-6 mb-6 qr-pulse"
          style={{
            background: 'white',
            borderRadius: 24,
            border: '2px solid #EEF1FF',
            boxShadow: 'var(--bb-shadow)',
          }}
        >
          <p style={{ fontSize: 12, fontWeight: 600, color: '#9BA3B8', marginBottom: 16 }}>SCAN TO VERIFY HANDOVER</p>
          <QRPattern />
          <p style={{ fontSize: 11, color: '#9BA3B8', marginTop: 16, fontWeight: 500 }}>
            Transaction ID: BB-{item.id.padStart(4, '0')}-2026
          </p>
        </div>

        {/* Details */}
        <div className="p-4 mb-4" style={{ background: '#EEF1FF', borderRadius: 18, border: '1px solid rgba(79,106,240,0.15)' }}>
          <p style={{ fontSize: 13, color: '#4F6AF0', fontWeight: 600, margin: 0 }}>
            📋 Show this QR code to <strong>{item.owner}</strong> during handover. Both parties need to be present.
          </p>
        </div>

        {/* Mark as returned */}
        <button
          onClick={() => setReturned(true)}
          style={{
            width: '100%',
            height: 56,
            borderRadius: 18,
            background: 'linear-gradient(135deg, #3BB8A2 0%, #4F6AF0 100%)',
            color: 'white',
            fontSize: 16,
            fontWeight: 700,
            border: 'none',
            cursor: 'pointer',
            boxShadow: '0 8px 24px rgba(59,184,162,0.35)',
          }}
        >
          Mark as Returned
        </button>
      </div>
      <HomeIndicator />
    </div>
  );
};
