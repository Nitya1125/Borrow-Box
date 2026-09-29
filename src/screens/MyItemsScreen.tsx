import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { BackIcon, AddIcon } from '../components/Icons';
import { myItems } from '../data';

interface Props {
  onBack: () => void;
  onAdd: () => void;
}

const statusConfig = {
  available: { label: 'Available', color: '#3BB8A2', bg: '#E6F7F4' },
  borrowed: { label: 'Borrowed', color: '#4F6AF0', bg: '#EEF1FF' },
  inactive: { label: 'Inactive', color: '#9BA3B8', bg: '#F4F6FF' },
};

const tabs = ['available', 'borrowed', 'inactive'] as const;

export const MyItemsScreen = ({ onBack, onAdd }: Props) => {
  const [activeTab, setActiveTab] = useState<typeof tabs[number]>('available');

  const filtered = myItems.filter(i => i.status === activeTab);

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div
        className="px-5 pt-3 pb-5"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }}>
              <BackIcon size={20} color="white" />
            </button>
            <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>My Items</h1>
          </div>
          <button
            onClick={onAdd}
            className="flex items-center gap-2"
            style={{
              height: 38,
              paddingLeft: 14,
              paddingRight: 14,
              borderRadius: 12,
              background: 'rgba(255,255,255,0.2)',
              border: '1px solid rgba(255,255,255,0.3)',
              color: 'white',
              fontSize: 13,
              fontWeight: 700,
              cursor: 'pointer',
            }}
          >
            <AddIcon size={16} color="white" />
            Add
          </button>
        </div>

        {/* Tabs */}
        <div className="flex gap-1 p-1" style={{ background: 'rgba(255,255,255,0.15)', borderRadius: 14 }}>
          {tabs.map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className="flex-1"
              style={{
                height: 36,
                borderRadius: 10,
                background: activeTab === tab ? 'white' : 'transparent',
                color: activeTab === tab ? '#4F6AF0' : 'rgba(255,255,255,0.75)',
                fontSize: 13,
                fontWeight: 700,
                border: 'none',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                textTransform: 'capitalize',
              }}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 screen-scroll px-5 pt-5 pb-4">
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <div style={{ fontSize: 56 }}>📦</div>
            <div className="text-center">
              <p style={{ fontSize: 16, fontWeight: 700, color: '#1C2135' }}>
                No {activeTab} items
              </p>
              <p style={{ fontSize: 13, color: '#9BA3B8', marginTop: 4 }}>
                {activeTab === 'available' ? 'List an item and help students near you!'
                  : activeTab === 'borrowed' ? 'None of your items are currently borrowed.'
                  : 'No inactive items.'}
              </p>
            </div>
            {activeTab === 'available' && (
              <button
                onClick={onAdd}
                style={{
                  padding: '12px 24px',
                  borderRadius: 14,
                  background: 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
                  color: 'white',
                  fontSize: 14,
                  fontWeight: 700,
                  border: 'none',
                  cursor: 'pointer',
                  boxShadow: '0 4px 16px rgba(79,106,240,0.3)',
                }}
              >
                + Add Your First Item
              </button>
            )}
          </div>
        ) : (
          <div className="stagger flex flex-col gap-3">
            {filtered.map(item => {
              const cfg = statusConfig[item.status];
              return (
                <div
                  key={item.id}
                  className="flex gap-3 p-4"
                  style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA', boxShadow: 'var(--bb-shadow-sm)' }}
                >
                  <img
                    src={item.image}
                    alt={item.name}
                    style={{ width: 72, height: 72, borderRadius: 16, objectFit: 'cover', flexShrink: 0 }}
                  />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0, lineHeight: 1.2 }}>
                        {item.name}
                      </p>
                      <span
                        style={{
                          fontSize: 10,
                          fontWeight: 700,
                          color: cfg.color,
                          background: cfg.bg,
                          borderRadius: 8,
                          padding: '3px 8px',
                          flexShrink: 0,
                        }}
                      >
                        {cfg.label}
                      </span>
                    </div>
                    <p style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500, margin: '0 0 6px' }}>{item.category}</p>
                    {item.currentBorrower && (
                      <p style={{ fontSize: 12, color: '#4F6AF0', fontWeight: 600, margin: '0 0 8px' }}>
                        👤 {item.currentBorrower}
                      </p>
                    )}
                    <div className="flex gap-2">
                      <button style={{ padding: '5px 12px', borderRadius: 8, background: '#EEF1FF', color: '#4F6AF0', fontSize: 12, fontWeight: 700, border: 'none', cursor: 'pointer' }}>
                        Edit
                      </button>
                      <button style={{ padding: '5px 12px', borderRadius: 8, background: '#FFF0F0', color: '#FF7A7A', fontSize: 12, fontWeight: 700, border: 'none', cursor: 'pointer' }}>
                        {item.status === 'inactive' ? 'Delete' : 'Deactivate'}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
