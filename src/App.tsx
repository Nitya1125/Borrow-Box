import React, { useState } from 'react';
import { PhoneShell } from './components/PhoneShell';
import { BottomNav } from './components/BottomNav';
import { SplashScreen } from './screens/SplashScreen';
import { OnboardingScreen } from './screens/OnboardingScreen';
import { LoginScreen } from './screens/LoginScreen';
import { SignupScreen } from './screens/SignupScreen';
import { HomeScreen } from './screens/HomeScreen';
import { ExploreScreen } from './screens/ExploreScreen';
import { SearchScreen } from './screens/SearchScreen';
import { ItemDetailsScreen } from './screens/ItemDetailsScreen';
import { BorrowRequestScreen } from './screens/BorrowRequestScreen';
import { QRScreen } from './screens/QRScreen';
import { ActivityScreen } from './screens/ActivityScreen';
import { AddItemScreen } from './screens/AddItemScreen';
import { NotificationsScreen } from './screens/NotificationsScreen';
import { ProfileScreen } from './screens/ProfileScreen';
import { HistoryScreen } from './screens/HistoryScreen';
import { RatingScreen } from './screens/RatingScreen';
import { MyItemsScreen } from './screens/MyItemsScreen';
import type { Item } from './data';
import { items as allItems } from './data';

type Screen =
  | 'splash'
  | 'onboarding'
  | 'login'
  | 'signup'
  | 'home'
  | 'explore'
  | 'search'
  | 'item-details'
  | 'borrow-request'
  | 'qr'
  | 'activity'
  | 'add'
  | 'notifications'
  | 'profile'
  | 'history'
  | 'rating'
  | 'my-items';

type BottomTab = 'home' | 'explore' | 'add' | 'activity' | 'profile';

// Screen navigation guide overlay
const ScreenGuide = ({ screens, current, onNavigate }: {
  screens: { id: Screen; label: string }[];
  current: Screen;
  onNavigate: (s: Screen) => void;
}) => (
  <div
    style={{
      position: 'absolute',
      top: 0,
      right: -280,
      width: 260,
      background: 'white',
      borderRadius: 20,
      padding: 16,
      boxShadow: '0 8px 40px rgba(0,0,0,0.12)',
      border: '1px solid #E5E8F2',
      maxHeight: 852,
      overflowY: 'auto',
    }}
  >
    <p style={{ fontSize: 11, fontWeight: 800, color: '#9BA3B8', letterSpacing: 1, marginBottom: 10, textTransform: 'uppercase' }}>
      Screen Navigator
    </p>
    <div className="flex flex-col gap-1">
      {screens.map(({ id, label }) => (
        <button
          key={id}
          onClick={() => onNavigate(id)}
          style={{
            padding: '8px 12px',
            borderRadius: 10,
            background: current === id ? '#EEF1FF' : 'transparent',
            border: `1.5px solid ${current === id ? '#4F6AF0' : 'transparent'}`,
            color: current === id ? '#4F6AF0' : '#5A607A',
            fontSize: 13,
            fontWeight: current === id ? 700 : 500,
            textAlign: 'left',
            cursor: 'pointer',
            transition: 'all 0.15s ease',
          }}
        >
          {current === id ? '▶ ' : '  '}{label}
        </button>
      ))}
    </div>
  </div>
);

const ALL_SCREENS: { id: Screen; label: string }[] = [
  { id: 'splash', label: 'Splash Screen' },
  { id: 'onboarding', label: 'Onboarding' },
  { id: 'login', label: 'Login' },
  { id: 'signup', label: 'Sign Up' },
  { id: 'home', label: 'Home' },
  { id: 'explore', label: 'Explore' },
  { id: 'search', label: 'Search' },
  { id: 'item-details', label: 'Item Details' },
  { id: 'borrow-request', label: 'Borrow Request' },
  { id: 'qr', label: 'QR Verification' },
  { id: 'activity', label: 'Activity' },
  { id: 'add', label: 'Add Item' },
  { id: 'notifications', label: 'Notifications' },
  { id: 'profile', label: 'Profile' },
  { id: 'history', label: 'Borrowing History' },
  { id: 'rating', label: 'Rating & Review' },
  { id: 'my-items', label: 'My Items' },
];

const MAIN_TABS: BottomTab[] = ['home', 'explore', 'add', 'activity', 'profile'];

export default function App() {
  const [screen, setScreen] = useState<Screen>('splash');
  const [activeTab, setActiveTab] = useState<BottomTab>('home');
  const [selectedItem, setSelectedItem] = useState<Item | null>(null);
  const [showGuide, setShowGuide] = useState(true);

  const go = (s: Screen) => {
    setScreen(s);
    if (MAIN_TABS.includes(s as BottomTab)) {
      setActiveTab(s as BottomTab);
    }
  };

  const handleTabNav = (tab: BottomTab) => {
    setActiveTab(tab);
    setScreen(tab);
  };

  const handleItem = (item: Item) => {
    setSelectedItem(item);
    go('item-details');
  };

  const showBottomNav = MAIN_TABS.includes(screen as BottomTab);

  const renderScreen = () => {
    switch (screen) {
      case 'splash':
        return <SplashScreen onDone={() => go('onboarding')} />;

      case 'onboarding':
        return <OnboardingScreen onDone={() => go('login')} />;

      case 'login':
        return (
          <LoginScreen
            onLogin={() => go('home')}
            onSignup={() => go('signup')}
          />
        );

      case 'signup':
        return (
          <SignupScreen
            onDone={() => go('home')}
            onBack={() => go('login')}
          />
        );

      case 'home':
        return (
          <HomeScreen
            onSearch={() => go('search')}
            onItem={handleItem}
            onNotifications={() => go('notifications')}
          />
        );

      case 'explore':
        return (
          <ExploreScreen
            onItem={handleItem}
            onSearch={() => go('search')}
          />
        );

      case 'search':
        return (
          <SearchScreen
            onBack={() => go(activeTab)}
            onItem={handleItem}
          />
        );

      case 'item-details':
        return selectedItem ? (
          <ItemDetailsScreen
            item={selectedItem}
            onBack={() => go(activeTab)}
            onBorrow={() => go('borrow-request')}
          />
        ) : null;

      case 'borrow-request':
        return selectedItem ? (
          <BorrowRequestScreen
            item={selectedItem}
            onBack={() => go('item-details')}
            onSuccess={() => go('activity')}
          />
        ) : null;

      case 'qr':
        return selectedItem ? (
          <QRScreen
            item={selectedItem}
            onBack={() => go('activity')}
          />
        ) : null;

      case 'activity':
        return (
          <ActivityScreen
            onQR={(item: Item) => {
              setSelectedItem(item);
              go('qr');
            }}
          />
        );

      case 'add':
        return (
          <AddItemScreen
            onBack={() => go('home')}
            onDone={() => go('home')}
          />
        );

      case 'notifications':
        return <NotificationsScreen onBack={() => go(activeTab)} />;

      case 'profile':
        return (
          <ProfileScreen
            onLogout={() => go('login')}
            onHistory={() => go('history')}
            onMyItems={() => go('my-items')}
            onRating={() => go('rating')}
          />
        );

      case 'history':
        return <HistoryScreen onBack={() => go('profile')} />;

      case 'rating':
        return (
          <RatingScreen
            onBack={() => go('profile')}
            onDone={() => go('home')}
          />
        );

      case 'my-items':
        return (
          <MyItemsScreen
            onBack={() => go('profile')}
            onAdd={() => go('add')}
          />
        );

      default:
        return null;
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center"
      style={{
        background: 'linear-gradient(135deg, #E8EBF6 0%, #D8DFF5 100%)',
        padding: '32px 20px',
      }}
    >
      {/* App title */}
      <div style={{ position: 'fixed', top: 24, left: '50%', transform: 'translateX(-50%)', zIndex: 100, textAlign: 'center' }}>
        <div className="flex items-center gap-3">
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 8,
              background: 'white',
              borderRadius: 16,
              padding: '8px 16px',
              boxShadow: '0 4px 20px rgba(79,106,240,0.15)',
              border: '1px solid #E5E8F2',
            }}
          >
            <svg width="24" height="24" viewBox="0 0 48 48" fill="none">
              <rect width="48" height="48" rx="12" fill="url(#bb-grad2)" />
              <rect x="10" y="18" width="28" height="20" rx="4" fill="white" fillOpacity="0.3" stroke="white" strokeWidth="1.5" />
              <path d="M10 24H38" stroke="white" strokeWidth="1.5" />
              <path d="M20 30H28M24 27L27 30L24 33" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              <defs>
                <linearGradient id="bb-grad2" x1="0" y1="0" x2="48" y2="48" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#4F6AF0" />
                  <stop offset="1" stopColor="#7B61FF" />
                </linearGradient>
              </defs>
            </svg>
            <span style={{ fontSize: 16, fontWeight: 800, color: '#1C2135' }}>BorrowBox</span>
            <span style={{ fontSize: 12, color: '#9BA3B8', fontWeight: 500 }}>UI Demo</span>
            <button
              onClick={() => setShowGuide(!showGuide)}
              style={{
                marginLeft: 4,
                padding: '3px 10px',
                borderRadius: 8,
                background: showGuide ? '#EEF1FF' : '#F4F6FF',
                border: `1px solid ${showGuide ? '#4F6AF0' : '#E5E8F2'}`,
                color: showGuide ? '#4F6AF0' : '#9BA3B8',
                fontSize: 12,
                fontWeight: 700,
                cursor: 'pointer',
              }}
            >
              {showGuide ? 'Hide' : 'Screens'}
            </button>
          </div>
        </div>
      </div>

      {/* Phone + guide */}
      <div className="relative" style={{ marginTop: 40 }}>
        <PhoneShell>
          <div className="flex flex-col h-full screen-enter" key={screen}>
            <div className="flex-1 min-h-0 overflow-hidden">
              {renderScreen()}
            </div>
            {showBottomNav && (
              <BottomNav
                active={activeTab}
                onNavigate={handleTabNav}
              />
            )}
          </div>
        </PhoneShell>

        {/* Screen navigator */}
        {showGuide && (
          <ScreenGuide
            screens={ALL_SCREENS}
            current={screen}
            onNavigate={s => {
              if (s === 'item-details' || s === 'borrow-request' || s === 'qr') {
                setSelectedItem(allItems[0]);
              }
              go(s);
            }}
          />
        )}
      </div>
    </div>
  );
}
