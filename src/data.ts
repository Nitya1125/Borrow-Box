export interface Item {
  id: string;
  name: string;
  category: string;
  image: string;
  owner: string;
  ownerAvatar: string;
  ownerCollege: string;
  ownerRating: number;
  available: boolean;
  description: string;
  distance: string;
  maxDuration: string;
  pickupMethod: string;
}

export interface Transaction {
  id: string;
  item: Item;
  person: string;
  personAvatar: string;
  status: 'pending' | 'approved' | 'borrowed' | 'returned' | 'rejected' | 'overdue';
  borrowDate: string;
  returnDate: string;
  type: 'borrowed' | 'lent';
}

export interface Notification {
  id: string;
  type: 'request' | 'accepted' | 'returned' | 'due' | 'reminder';
  title: string;
  message: string;
  time: string;
  read: boolean;
}

export const categories = [
  { id: 'all', label: 'All', icon: '◎' },
  { id: 'books', label: 'Books', icon: '📚' },
  { id: 'electronics', label: 'Electronics', icon: '⚡' },
  { id: 'calculators', label: 'Calculators', icon: '🔢' },
  { id: 'stationery', label: 'Stationery', icon: '✏️' },
  { id: 'sports', label: 'Sports', icon: '⚽' },
  { id: 'lab', label: 'Lab', icon: '🔬' },
  { id: 'others', label: 'Others', icon: '📦' },
];

export const items: Item[] = [
  {
    id: '1',
    name: 'Casio FX-991EX Calculator',
    category: 'Calculators',
    image: 'https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=300&fit=crop&auto=format',
    owner: 'Arjun Mehta',
    ownerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'BITS Pilani',
    ownerRating: 4.8,
    available: true,
    description: 'Scientific calculator, perfect for engineering and physics exams. Battery is new. Handle with care.',
    distance: '0.3 km',
    maxDuration: '3 days',
    pickupMethod: 'Library gate',
  },
  {
    id: '2',
    name: 'Data Structures — Cormen',
    category: 'Books',
    image: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format',
    owner: 'Priya Sharma',
    ownerAvatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b604?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'IIT Delhi',
    ownerRating: 4.9,
    available: true,
    description: 'Introduction to Algorithms, 3rd edition. Good condition, some margin notes. Great for interviews.',
    distance: '0.7 km',
    maxDuration: '7 days',
    pickupMethod: 'Hostel C, Room 204',
  },
  {
    id: '3',
    name: 'Sony WH-1000XM4 Headphones',
    category: 'Electronics',
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop&auto=format',
    owner: 'Rahul Gupta',
    ownerAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'NIT Trichy',
    ownerRating: 4.7,
    available: false,
    description: 'Premium noise-cancelling headphones. Great for study sessions. Comes with carry case.',
    distance: '1.2 km',
    maxDuration: '2 days',
    pickupMethod: 'Academic block A',
  },
  {
    id: '4',
    name: 'USB-C Charging Cable (1m)',
    category: 'Electronics',
    image: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop&auto=format',
    owner: 'Sneha Patel',
    ownerAvatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'VIT Vellore',
    ownerRating: 4.6,
    available: true,
    description: 'Fast charging USB-C cable. Works with most Android phones and laptops.',
    distance: '0.1 km',
    maxDuration: '1 day',
    pickupMethod: 'Canteen area',
  },
  {
    id: '5',
    name: 'Lab Coat (Size M)',
    category: 'Lab',
    image: 'https://images.unsplash.com/photo-1576671081837-49000212a370?w=400&h=300&fit=crop&auto=format',
    owner: 'Vikram Singh',
    ownerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'SRM University',
    ownerRating: 4.5,
    available: true,
    description: 'White lab coat, size medium. Clean and pressed. Perfect for chemistry and bio practicals.',
    distance: '0.4 km',
    maxDuration: '5 days',
    pickupMethod: 'Lab 3 entrance',
  },
  {
    id: '6',
    name: 'Badminton Racket Set',
    category: 'Sports',
    image: 'https://images.unsplash.com/photo-1592656094267-764a45160876?w=400&h=300&fit=crop&auto=format',
    owner: 'Meera Krishnan',
    ownerAvatar: 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'Manipal University',
    ownerRating: 4.9,
    available: true,
    description: 'Set of 2 rackets with 3 shuttlecocks. Good quality Yonex rackets for casual play.',
    distance: '0.5 km',
    maxDuration: '3 days',
    pickupMethod: 'Sports complex reception',
  },
  {
    id: '7',
    name: 'Drawing Kit (Professional)',
    category: 'Stationery',
    image: 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=400&h=300&fit=crop&auto=format',
    owner: 'Ananya Roy',
    ownerAvatar: 'https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'NIFT Delhi',
    ownerRating: 4.8,
    available: true,
    description: 'Complete drawing kit with pencils, charcoals, erasers and blending tools. For architecture and design students.',
    distance: '0.9 km',
    maxDuration: '7 days',
    pickupMethod: 'Design studio, Block D',
  },
  {
    id: '8',
    name: 'Raspberry Pi 4B (4GB)',
    category: 'Electronics',
    image: 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=300&fit=crop&auto=format',
    owner: 'Deepak Nair',
    ownerAvatar: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=80&h=80&fit=crop&auto=format',
    ownerCollege: 'IIIT Hyderabad',
    ownerRating: 4.7,
    available: true,
    description: 'Raspberry Pi 4B 4GB RAM with power adapter and 32GB SD card. Ideal for IoT and ML projects.',
    distance: '1.5 km',
    maxDuration: '5 days',
    pickupMethod: 'CS dept common room',
  },
];

export const transactions: Transaction[] = [
  {
    id: 't1',
    item: items[1],
    person: 'Priya Sharma',
    personAvatar: items[1].ownerAvatar,
    status: 'borrowed',
    borrowDate: '24 Sep 2026',
    returnDate: '01 Oct 2026',
    type: 'borrowed',
  },
  {
    id: 't2',
    item: items[0],
    person: 'Karan Verma',
    personAvatar: 'https://images.unsplash.com/photo-1463453091185-61582044d556?w=80&h=80&fit=crop&auto=format',
    status: 'pending',
    borrowDate: '29 Sep 2026',
    returnDate: '02 Oct 2026',
    type: 'lent',
  },
  {
    id: 't3',
    item: items[3],
    person: 'Sneha Patel',
    personAvatar: items[3].ownerAvatar,
    status: 'returned',
    borrowDate: '20 Sep 2026',
    returnDate: '21 Sep 2026',
    type: 'borrowed',
  },
  {
    id: 't4',
    item: items[5],
    person: 'Rohan Das',
    personAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format',
    status: 'approved',
    borrowDate: '30 Sep 2026',
    returnDate: '03 Oct 2026',
    type: 'lent',
  },
  {
    id: 't5',
    item: items[6],
    person: 'Ananya Roy',
    personAvatar: items[6].ownerAvatar,
    status: 'overdue',
    borrowDate: '18 Sep 2026',
    returnDate: '25 Sep 2026',
    type: 'borrowed',
  },
];

export const notifications: Notification[] = [
  {
    id: 'n1',
    type: 'accepted',
    title: 'Request Accepted!',
    message: 'Priya accepted your request for Data Structures book.',
    time: '2 min ago',
    read: false,
  },
  {
    id: 'n2',
    type: 'request',
    title: 'New Borrow Request',
    message: 'Karan wants to borrow your Casio FX-991EX Calculator.',
    time: '1 hr ago',
    read: false,
  },
  {
    id: 'n3',
    type: 'due',
    title: 'Return Due Tomorrow',
    message: 'Your borrowed Data Structures book is due tomorrow.',
    time: '3 hr ago',
    read: false,
  },
  {
    id: 'n4',
    type: 'returned',
    title: 'Item Returned',
    message: 'Sneha returned the USB-C Cable you lent.',
    time: '1 day ago',
    read: true,
  },
  {
    id: 'n5',
    type: 'reminder',
    title: 'Wishlist Update',
    message: 'Sony WH-1000XM4 Headphones is now available.',
    time: '2 days ago',
    read: true,
  },
];

export const myItems: (Item & { status: 'available' | 'borrowed' | 'inactive'; currentBorrower?: string })[] = [
  { ...items[0], status: 'available' },
  { ...items[5], status: 'borrowed', currentBorrower: 'Rohan Das' },
  { ...items[2], status: 'inactive' },
];
