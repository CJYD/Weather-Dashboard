// Weather Dashboard JavaScript

// Favorites management using localStorage
const FavoritesManager = {
    KEY: 'weatherDashboardFavorites',
    MAX_FAVORITES: 5,

    // Get all favorites
    getFavorites() {
        const stored = localStorage.getItem(this.KEY);
        return stored ? JSON.parse(stored) : [];
    },

    // Add a city to favorites
    addFavorite(city) {
        let favorites = this.getFavorites();

        // Check if already exists
        if (favorites.includes(city)) {
            return false;
        }

        // Check max limit
        if (favorites.length >= this.MAX_FAVORITES) {
            alert(`Maximum ${this.MAX_FAVORITES} favorites allowed`);
            return false;
        }

        favorites.push(city);
        localStorage.setItem(this.KEY, JSON.stringify(favorites));
        return true;
    },

    // Remove a city from favorites
    removeFavorite(city) {
        let favorites = this.getFavorites();
        favorites = favorites.filter(f => f !== city);
        localStorage.setItem(this.KEY, JSON.stringify(favorites));
    },

    // Check if city is favorited
    isFavorite(city) {
        return this.getFavorites().includes(city);
    }
};

// Unit conversion toggle
function toggleTemperatureUnit() {
    const currentUnit = document.querySelector('select[name="unit"]').value;
    const newUnit = currentUnit === 'CELSIUS' ? 'FAHRENHEIT' : 'CELSIUS';

    // Update the form and resubmit
    document.querySelector('select[name="unit"]').value = newUnit;

    // If we have a city displayed, reload with new unit
    const cityInput = document.querySelector('input[name="city"]');
    if (cityInput && cityInput.value) {
        document.querySelector('.search-form').submit();
    }
}

// Add keyboard shortcut for search (Ctrl/Cmd + K)
document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.querySelector('input[name="city"]').focus();
    }
});

// Add loading state to search button
document.querySelector('.search-form')?.addEventListener('submit', (e) => {
    const button = e.target.querySelector('button[type="submit"]');
    button.innerHTML = '<span class="loading"></span> Loading...';
    button.disabled = true;
});

// Auto-focus search input on page load
window.addEventListener('load', () => {
    const searchInput = document.querySelector('input[name="city"]');
    if (searchInput && !searchInput.value) {
        searchInput.focus();
    }
});

// Add animation to weather cards on load
document.addEventListener('DOMContentLoaded', () => {
    const weatherCard = document.querySelector('.weather-card');
    if (weatherCard) {
        weatherCard.style.opacity = '0';
        setTimeout(() => {
            weatherCard.style.transition = 'opacity 0.5s ease';
            weatherCard.style.opacity = '1';
        }, 100);
    }

    // Animate forecast cards
    const forecastCards = document.querySelectorAll('.forecast-card');
    forecastCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, 200 + (index * 100));
    });
});

// Add tooltip for technical details
function initTooltips() {
    const detailItems = document.querySelectorAll('.detail-item');
    detailItems.forEach(item => {
        item.setAttribute('title', 'Click for more details');
    });
}

// Call tooltip initialization
document.addEventListener('DOMContentLoaded', initTooltips);

// Add print functionality (Ctrl/Cmd + P)
document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        window.print();
    }
});

// Service worker registration for offline capability (optional enhancement)
if ('serviceWorker' in navigator) {
    // Uncomment to enable service worker
    // navigator.serviceWorker.register('/sw.js')
    //     .then(() => console.log('Service Worker registered'))
    //     .catch(err => console.log('Service Worker registration failed', err));
}

// Add refresh button functionality
function refreshWeather() {
    const cityInput = document.querySelector('input[name="city"]');
    if (cityInput && cityInput.value) {
        // Clear cache by adding timestamp to prevent caching
        const form = document.querySelector('.search-form');
        form.submit();
    }
}

// Add geolocation support (future enhancement)
function getCurrentLocation() {
    if ('geolocation' in navigator) {
        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const { latitude, longitude } = position.coords;
                // This would require a reverse geocoding endpoint
                console.log('Current location:', latitude, longitude);
                alert('Geolocation feature coming soon!');
            },
            (error) => {
                console.error('Geolocation error:', error);
                alert('Unable to get your location');
            }
        );
    } else {
        alert('Geolocation is not supported by your browser');
    }
}

// Export functions for potential use
window.WeatherDashboard = {
    FavoritesManager,
    toggleTemperatureUnit,
    refreshWeather,
    getCurrentLocation
};

console.log('Weather Dashboard loaded successfully');
