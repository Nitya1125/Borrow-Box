import React from 'react';

interface StatusBarProps {
  light?: boolean;
}

export const StatusBar = ({ light = false }: StatusBarProps) => {
  const textColor = light ? 'text-white' : 'text-[#1C2135]';
  return (
    <div className={`flex items-center justify-between px-6 pt-2 pb-1 h-11 flex-shrink-0 ${textColor}`} style={{ fontSize: 12, fontWeight: 600 }}>
      <span>9:41</span>
      <div className="flex items-center gap-1.5">
        {/* Signal */}
        <svg width="16" height="12" viewBox="0 0 16 12" fill="currentColor">
          <rect x="0" y="9" width="3" height="3" rx="0.5" />
          <rect x="4.5" y="6" width="3" height="6" rx="0.5" />
          <rect x="9" y="3" width="3" height="9" rx="0.5" />
          <rect x="13.5" y="0" width="2.5" height="12" rx="0.5" opacity="0.3" />
        </svg>
        {/* Wifi */}
        <svg width="16" height="12" viewBox="0 0 16 12" fill="currentColor">
          <path d="M8 9.5C8.83 9.5 9.5 10.17 9.5 11S8.83 12.5 8 12.5 6.5 11.83 6.5 11 7.17 9.5 8 9.5Z" />
          <path d="M3.5 7C5.0 5.5 6.9 4.5 8 4.5C9.1 4.5 11.0 5.5 12.5 7" stroke="currentColor" strokeWidth="1.2" fill="none" strokeLinecap="round" />
          <path d="M1 4.5C3.2 2.3 5.5 1 8 1C10.5 1 12.8 2.3 15 4.5" stroke="currentColor" strokeWidth="1.2" fill="none" strokeLinecap="round" />
        </svg>
        {/* Battery */}
        <div className="flex items-center">
          <div className="w-6 h-3 rounded-sm border border-current relative" style={{ borderWidth: 1.2 }}>
            <div className="absolute inset-0.5 rounded-sm" style={{ background: 'currentColor', width: '75%' }} />
          </div>
          <div className="w-0.5 h-1.5 rounded-r-sm" style={{ background: 'currentColor', marginLeft: 1 }} />
        </div>
      </div>
    </div>
  );
};

export const HomeIndicator = ({ light = false }: { light?: boolean }) => (
  <div className="flex justify-center py-2 flex-shrink-0">
    <div
      className="rounded-full"
      style={{
        width: 134,
        height: 5,
        background: light ? 'rgba(255,255,255,0.5)' : 'rgba(28,33,53,0.2)',
      }}
    />
  </div>
);

interface PhoneShellProps {
  children: React.ReactNode;
}

export const PhoneShell = ({ children }: PhoneShellProps) => {
  return (
    <div
      className="relative mx-auto flex-shrink-0"
      style={{
        width: 393,
        height: 852,
        background: '#1a1a2e',
        borderRadius: 54,
        padding: 12,
        boxShadow: '0 40px 100px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.08) inset, 0 2px 4px rgba(0,0,0,0.5)',
      }}
    >
      {/* Side buttons */}
      <div
        className="absolute rounded-full"
        style={{ left: -3, top: 130, width: 3, height: 32, background: '#2a2a3e' }}
      />
      <div
        className="absolute rounded-full"
        style={{ left: -3, top: 174, width: 3, height: 64, background: '#2a2a3e' }}
      />
      <div
        className="absolute rounded-full"
        style={{ left: -3, top: 250, width: 3, height: 64, background: '#2a2a3e' }}
      />
      <div
        className="absolute rounded-full"
        style={{ right: -3, top: 160, width: 3, height: 80, background: '#2a2a3e' }}
      />
      {/* Screen */}
      <div
        className="relative overflow-hidden"
        style={{
          width: '100%',
          height: '100%',
          borderRadius: 44,
          background: '#F4F6FF',
        }}
      >
        {/* Notch / punch-hole camera */}
        <div
          className="absolute top-3.5 left-1/2 -translate-x-1/2 z-50"
          style={{
            width: 12,
            height: 12,
            background: '#111',
            borderRadius: '50%',
          }}
        />
        {children}
      </div>
    </div>
  );
};
