import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { QRIcon, CheckIcon } from '../components/Icons';
import { transactions } from '../data';
import type { Transaction } from '../data';

const statusColors: Record<Transaction['status'], { bg: string; text: string; label: string }> = {
  pending: { bg: '#FEF8E7', text: '#F5C842', label: 'Pending' },
  approved: { bg: '#EEF1FF', text: '#4F6AF0', label: 'Approved' },
  borrowed: { bg: '#E6F7F4', text: '#3BB8A2', label: 'Borrowed' },
  returned: { bg: '#F0F2FA', text: '#9BA3B8', label: 'Returned' },
  rejected: { bg: '#FFF0F0', text: '#FF7A7A', label: 'Rejected' },
  overdue: { bg: '#FFF0F0', text: '#FF7A7A', label: 'Overdue ⚠️' },
};

const tabs = ['Borrowed', 'Lent', 'Requests'];

interface Props {
  onQR: (item: any) => void;
}

const TransactionCard = ({ tx, onQR }: { tx: Transaction; onQR: () => void }) => {
  const s = statusColors[tx.status];
  return (
    <div
      className="flex gap-3 p-4"
      style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA', boxShadow: 'var(--bb-shadow-sm)' }}
    >
      <img
        src={tx.item.image}
        alt={tx.item.name}
        style={{ width: 64, height: 64, borderRadius: 14, objectFit: 'cover', flexShrink: 0 }}
      />
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2 mb-1">
          <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0, lineHeight: 1.2 }}>
            {tx.item.name}
          </p>
          <span
            style={{
              fontSize: 10,
              fontWeight: 700,
              color: s.text,
              background: s.bg,
              borderRadius: 8,
              padding: '3px 8px',
              flexShrink: 0,
            }}
          >
            {s.label}
          </span>
        </div>

        <div className="flex items-center gap-2 mb-2">
          <img src={tx.personAvatar} alt={tx.person} style={{ width: 18, height: 18, borderRadius: '50%', objectFit: 'cover' }} />
          <span style={{ fontSize: 12, color: '#5A607A', fontWeight: 600 }}>{tx.person}</span>
        </div>

        <div className="flex items-center gap-3">
          <span style={{ fontSize: 11, color: '#9BA3B8' }}>
            {tx.type === 'borrowed' ? 'Borrowed' : 'Lent'}: {tx.borrowDate}
          </span>
          {tx.status === 'approved' || tx.status === 'borrowed' ? (
            <button
              onClick={onQR}
              className="flex items-center gap-1"
              style={{ fontSize: 11, fontWeight: 700, color: '#4F6AF0', background: '#EEF1FF', borderRadius: 8, padding: '3px 8px', border: 'none', cursor: 'pointer' }}
            >
              <QRIcon size={12} color="#4F6AF0" />
              QR Code
            </button>
          ) : null}
        </div>
      </div>
    </div>
  );
};

const RequestCard = ({ tx }: { tx: Transaction }) => {
  const [accepted, setAccepted] = useState<boolean | null>(null);
  return (
    <div
      className="p-4"
      style={{ background: 'white', borderRadius: 20, border: '1px solid #F0F2FA', boxShadow: 'var(--bb-shadow-sm)' }}
    >
      <div className="flex gap-3 mb-4">
        <img src={tx.personAvatar} alt={tx.person} style={{ width: 48, height: 48, borderRadius: 14, objectFit: 'cover' }} />
        <div>
          <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>
            {tx.person} wants to borrow
          </p>
          <p style={{ fontSize: 13, fontWeight: 600, color: '#4F6AF0', margin: '2px 0' }}>{tx.item.name}</p>
          <p style={{ fontSize: 12, color: '#9BA3B8' }}>Return: {tx.returnDate}</p>
        </div>
      </div>

      {accepted === null ? (
        <div className="flex gap-2">
          <button
            onClick={() => setAccepted(true)}
            style={{
              flex: 1,
              height: 44,
              borderRadius: 14,
              background: 'linear-gradient(135deg, #3BB8A2 0%, #4F6AF0 100%)',
              color: 'white',
              fontSize: 14,
              fontWeight: 700,
              border: 'none',
              cursor: 'pointer',
              boxShadow: '0 4px 12px rgba(59,184,162,0.3)',
            }}
          >
            Accept
          </button>
          <button
            onClick={() => setAccepted(false)}
            style={{
              flex: 1,
              height: 44,
              borderRadius: 14,
              background: '#FFF0F0',
              color: '#FF7A7A',
              fontSize: 14,
              fontWeight: 700,
              border: '1.5px solid #FFCECE',
              cursor: 'pointer',
            }}
          >
            Reject
          </button>
        </div>
      ) : (
        <div
          className="flex items-center justify-center gap-2 py-2"
          style={{
            background: accepted ? '#E6F7F4' : '#FFF0F0',
            borderRadius: 12,
          }}
        >
          <span style={{ fontSize: 14, fontWeight: 700, color: accepted ? '#3BB8A2' : '#FF7A7A' }}>
            {accepted ? '✓ Request Accepted' : '✗ Request Rejected'}
          </span>
        </div>
      )}
    </div>
  );
};

export const ActivityScreen = ({ onQR }: Props) => {
  const [activeTab, setActiveTab] = useState('Borrowed');

  const borrowed = transactions.filter(t => t.type === 'borrowed');
  const lent = transactions.filter(t => t.type === 'lent');
  const requests = transactions.filter(t => t.type === 'lent' && (t.status === 'pending' || t.status === 'approved'));

  const current = activeTab === 'Borrowed' ? borrowed : activeTab === 'Lent' ? lent : requests;

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div className="px-5 pt-3 pb-4">
        <h1 style={{ fontSize: 24, fontWeight: 800, color: '#1C2135', marginBottom: 16 }}>Activity</h1>

        {/* Tabs */}
        <div className="flex gap-1 p-1" style={{ background: 'white', borderRadius: 16, border: '1px solid #F0F2FA' }}>
          {tabs.map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className="flex-1"
              style={{
                height: 38,
                borderRadius: 12,
                background: activeTab === tab ? '#4F6AF0' : 'transparent',
                color: activeTab === tab ? 'white' : '#9BA3B8',
                fontSize: 13,
                fontWeight: 700,
                border: 'none',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                boxShadow: activeTab === tab ? '0 2px 8px rgba(79,106,240,0.25)' : 'none',
              }}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      {/* List */}
      <div className="flex-1 screen-scroll px-5 pb-4">
        {current.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
              <circle cx="40" cy="40" r="36" fill="#EEF1FF" />
              <rect x="24" y="24" width="32" height="32" rx="8" fill="none" stroke="#9BA3B8" strokeWidth="2.5" />
              <path d="M32 40H48M40 32V48" stroke="#C5C9D8" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <div className="text-center">
              <p style={{ fontSize: 16, fontWeight: 700, color: '#1C2135' }}>Nothing here yet</p>
              <p style={{ fontSize: 13, color: '#9BA3B8', marginTop: 4 }}>
                {activeTab === 'Borrowed' ? 'Start borrowing items from students near you!'
                  : activeTab === 'Lent' ? 'List your items and help others!'
                  : 'No incoming requests yet.'}
              </p>
            </div>
          </div>
        ) : (
          <div className="stagger flex flex-col gap-3">
            {current.map(tx =>
              activeTab === 'Requests' ? (
                <RequestCard key={tx.id} tx={tx} />
              ) : (
                <TransactionCard key={tx.id} tx={tx} onQR={() => onQR(tx.item)} />
              )
            )}
          </div>
        )}
      </div>
    </div>
  );
};
