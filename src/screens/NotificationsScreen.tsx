import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { BackIcon, BellIcon, CheckIcon } from '../components/Icons';
import { notifications } from '../data';

interface Props {
  onBack: () => void;
}

const typeConfig = {
  request: { color: '#4F6AF0', bg: '#EEF1FF', icon: '📥' },
  accepted: { color: '#3BB8A2', bg: '#E6F7F4', icon: '✅' },
  returned: { color: '#9585E0', bg: '#F0EDFC', icon: '📦' },
  due: { color: '#FF7A7A', bg: '#FFF0F0', icon: '⏰' },
  reminder: { color: '#F5C842', bg: '#FEF8E7', icon: '🔔' },
};

export const NotificationsScreen = ({ onBack }: Props) => {
  const [notifs, setNotifs] = useState(notifications);

  const markAll = () => setNotifs(n => n.map(x => ({ ...x, read: true })));
  const unread = notifs.filter(n => !n.read).length;

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div
        className="flex items-center justify-between px-5 pt-3 pb-5"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        <div className="flex items-center gap-3">
          <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }}>
            <BackIcon size={20} color="white" />
          </button>
          <div>
            <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>Notifications</h1>
            {unread > 0 && <p style={{ fontSize: 12, color: 'rgba(255,255,255,0.75)', margin: 0 }}>{unread} unread</p>}
          </div>
        </div>
        {unread > 0 && (
          <button
            onClick={markAll}
            style={{ fontSize: 12, fontWeight: 700, color: 'rgba(255,255,255,0.85)', background: 'rgba(255,255,255,0.2)', borderRadius: 10, padding: '6px 12px', border: 'none', cursor: 'pointer' }}
          >
            Mark all read
          </button>
        )}
      </div>

      <div className="flex-1 screen-scroll px-5 pt-5 pb-4">
        {notifs.length === 0 ? (
          <div className="flex flex-col items-center py-16 gap-4">
            <div style={{ width: 80, height: 80, borderRadius: 24, background: '#EEF1FF', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <BellIcon size={36} color="#9BA3B8" />
            </div>
            <div className="text-center">
              <p style={{ fontSize: 16, fontWeight: 700, color: '#1C2135' }}>All caught up!</p>
              <p style={{ fontSize: 13, color: '#9BA3B8', marginTop: 4 }}>No notifications right now.</p>
            </div>
          </div>
        ) : (
          <div className="stagger flex flex-col gap-3">
            {notifs.map(n => {
              const cfg = typeConfig[n.type];
              return (
                <div
                  key={n.id}
                  onClick={() => setNotifs(prev => prev.map(x => x.id === n.id ? { ...x, read: true } : x))}
                  className="flex gap-3 p-4 card-press"
                  style={{
                    background: 'white',
                    borderRadius: 18,
                    border: `1.5px solid ${!n.read ? cfg.color + '30' : '#F0F2FA'}`,
                    boxShadow: !n.read ? `0 2px 12px ${cfg.color}15` : 'var(--bb-shadow-sm)',
                    position: 'relative',
                  }}
                >
                  {/* Unread dot */}
                  {!n.read && (
                    <div style={{ position: 'absolute', top: 14, right: 14, width: 8, height: 8, borderRadius: '50%', background: cfg.color }} />
                  )}

                  <div
                    style={{
                      width: 44,
                      height: 44,
                      borderRadius: 14,
                      background: cfg.bg,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      fontSize: 20,
                    }}
                  >
                    {cfg.icon}
                  </div>
                  <div className="flex-1 min-w-0 pr-4">
                    <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: '0 0 2px' }}>{n.title}</p>
                    <p style={{ fontSize: 13, color: '#5A607A', margin: '0 0 4px', lineHeight: 1.4 }}>{n.message}</p>
                    <p style={{ fontSize: 11, color: '#9BA3B8', margin: 0 }}>{n.time}</p>
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
