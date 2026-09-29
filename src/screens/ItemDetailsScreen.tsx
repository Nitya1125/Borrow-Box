import React, { useState } from 'react';
import { HomeIndicator } from '../components/PhoneShell';
import { BackIcon, BookmarkIcon, ShareIcon, StarIcon, LocationIcon, CalendarIcon } from '../components/Icons';
import type { Item } from '../data';

interface Props {
  item: Item;
  onBack: () => void;
  onBorrow: () => void;
}

export const ItemDetailsScreen = ({ item, onBack, onBorrow }: Props) => {
  const [saved, setSaved] = useState(false);

  const stars = Math.round(item.ownerRating);

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      {/* Hero image */}
      <div className="relative flex-shrink-0" style={{ height: 280, background: '#EEF1FF' }}>
        <img src={item.image} alt={item.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />

        {/* Gradient overlay */}
        <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(to bottom, rgba(0,0,0,0.25) 0%, transparent 50%)' }} />

        {/* Top bar */}
        <div className="absolute top-0 left-0 right-0 flex items-center justify-between px-4 pt-12">
          <button
            onClick={onBack}
            className="flex items-center justify-center"
            style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.9)', backdropFilter: 'blur(8px)' }}
          >
            <BackIcon size={20} color="#1C2135" />
          </button>
          <div className="flex gap-2">
            <button
              onClick={() => setSaved(!saved)}
              className="flex items-center justify-center"
              style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.9)', backdropFilter: 'blur(8px)' }}
            >
              <BookmarkIcon size={18} color={saved ? '#4F6AF0' : '#1C2135'} filled={saved} />
            </button>
            <button
              className="flex items-center justify-center"
              style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.9)', backdropFilter: 'blur(8px)' }}
            >
              <ShareIcon size={18} color="#1C2135" />
            </button>
          </div>
        </div>

        {/* Status badge */}
        <div className="absolute bottom-4 left-4">
          <span
            style={{
              fontSize: 12,
              fontWeight: 700,
              color: item.available ? '#3BB8A2' : '#FF7A7A',
              background: item.available ? 'rgba(230,247,244,0.95)' : 'rgba(255,240,240,0.95)',
              borderRadius: 10,
              padding: '5px 12px',
              backdropFilter: 'blur(4px)',
            }}
          >
            ● {item.available ? 'Available to Borrow' : 'Currently Borrowed'}
          </span>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 screen-scroll">
        <div className="px-5 pt-5 pb-4">
          {/* Title */}
          <div className="flex items-start justify-between gap-3 mb-1">
            <h1 style={{ fontSize: 22, fontWeight: 800, color: '#1C2135', lineHeight: 1.2, margin: 0 }}>
              {item.name}
            </h1>
          </div>
          <div className="flex items-center gap-2 mb-4">
            <span
              style={{
                fontSize: 12,
                fontWeight: 700,
                color: '#9585E0',
                background: '#F0EDFC',
                borderRadius: 8,
                padding: '3px 10px',
              }}
            >
              {item.category}
            </span>
            <div className="flex items-center gap-1">
              <LocationIcon size={13} color="#9BA3B8" />
              <span style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500 }}>{item.distance} away</span>
            </div>
          </div>

          {/* Description */}
          <div
            className="p-4 mb-4"
            style={{ background: 'white', borderRadius: 18, border: '1px solid #F0F2FA' }}
          >
            <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 8 }}>About this item</h3>
            <p style={{ fontSize: 14, color: '#5A607A', lineHeight: 1.6, margin: 0 }}>{item.description}</p>
          </div>

          {/* Owner card */}
          <div
            className="p-4 mb-4 flex items-center gap-3"
            style={{ background: 'white', borderRadius: 18, border: '1px solid #F0F2FA' }}
          >
            <img
              src={item.ownerAvatar}
              alt={item.owner}
              style={{ width: 52, height: 52, borderRadius: 16, objectFit: 'cover' }}
            />
            <div className="flex-1">
              <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>{item.owner}</p>
              <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: '2px 0 4px' }}>{item.ownerCollege}</p>
              <div className="flex items-center gap-1">
                {[1, 2, 3, 4, 5].map(i => (
                  <StarIcon key={i} size={12} color="#F5C842" filled={i <= stars} />
                ))}
                <span style={{ fontSize: 12, fontWeight: 700, color: '#5A607A', marginLeft: 4 }}>{item.ownerRating}</span>
              </div>
            </div>
            <button
              style={{
                padding: '8px 14px',
                borderRadius: 12,
                background: '#EEF1FF',
                color: '#4F6AF0',
                fontSize: 12,
                fontWeight: 700,
                border: 'none',
                cursor: 'pointer',
              }}
            >
              Message
            </button>
          </div>

          {/* Borrow details */}
          <div
            className="p-4 mb-4"
            style={{ background: 'white', borderRadius: 18, border: '1px solid #F0F2FA' }}
          >
            <h3 style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', marginBottom: 12 }}>Borrowing Details</h3>
            {[
              { icon: <CalendarIcon size={16} color="#4F6AF0" />, label: 'Available from', value: 'Today' },
              { icon: <CalendarIcon size={16} color="#3BB8A2" />, label: 'Max duration', value: item.maxDuration },
              { icon: <LocationIcon size={16} color="#9585E0" />, label: 'Pickup / Handover', value: item.pickupMethod },
            ].map(({ icon, label, value }) => (
              <div key={label} className="flex items-center gap-3 mb-3 last:mb-0">
                <div
                  style={{
                    width: 36,
                    height: 36,
                    borderRadius: 10,
                    background: '#F4F6FF',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0,
                  }}
                >
                  {icon}
                </div>
                <div>
                  <p style={{ fontSize: 11, color: '#9BA3B8', fontWeight: 500, margin: 0 }}>{label}</p>
                  <p style={{ fontSize: 14, color: '#1C2135', fontWeight: 600, margin: 0 }}>{value}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Sticky bottom action */}
      <div
        className="flex-shrink-0 px-5 pt-3 pb-2"
        style={{
          background: 'white',
          borderTop: '1px solid #E5E8F2',
          boxShadow: '0 -4px 20px rgba(79,106,240,0.08)',
        }}
      >
        <button
          onClick={onBorrow}
          className="w-full ripple"
          disabled={!item.available}
          style={{
            height: 56,
            borderRadius: 18,
            background: item.available ? 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)' : '#E5E8F2',
            color: item.available ? 'white' : '#9BA3B8',
            fontSize: 16,
            fontWeight: 700,
            border: 'none',
            cursor: item.available ? 'pointer' : 'not-allowed',
            boxShadow: item.available ? '0 8px 24px rgba(79,106,240,0.35)' : 'none',
          }}
        >
          {item.available ? 'Request to Borrow' : 'Currently Unavailable'}
        </button>
      </div>
      <HomeIndicator />
    </div>
  );
};
