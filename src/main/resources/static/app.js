const apiBaseUrl = '/api/notes';

// DOM Elements
const notesGrid = document.getElementById('notes-grid');
const addNoteBtn = document.getElementById('add-note-btn');
const modalOverlay = document.getElementById('modal-overlay');
const closeModalBtn = document.getElementById('close-modal');
const cancelBtn = document.getElementById('cancel-btn');
const noteForm = document.getElementById('note-form');
const modalTitle = document.getElementById('modal-title');
const emptyState = document.getElementById('empty-state');
const logoutBtn = document.getElementById('logout-btn');

// State
let isEditing = false;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    fetchNotes();
});

// Event Listeners
addNoteBtn.addEventListener('click', () => {
    openModal('New Note');
});

closeModalBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);

modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) closeModal();
});

logoutBtn.addEventListener('click', async () => {
    try {
        await fetch('/logout', { method: 'POST' });
        window.location.href = '/login';
    } catch (error) {
        console.error('Logout failed:', error);
        window.location.href = '/login';
    }
});

noteForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('note-id').value;
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    const noteData = { title, content };

    try {
        if (isEditing) {
            await updateNote(id, noteData);
        } else {
            await createNote(noteData);
        }
        closeModal();
        fetchNotes();
    } catch (error) {
        console.error('Error saving note:', error);
        alert('Failed to save note. Please try again.');
    }
});

// Functions
async function fetchNotes() {
    try {
        const response = await fetch(apiBaseUrl);

        if (response.status === 401 || response.status === 403) {
            window.location.href = '/login';
            return;
        }

        const notes = await response.json();
        renderNotes(notes);
    } catch (error) {
        console.error('Error fetching notes:', error);
    }
}

async function createNote(noteData) {
    const response = await fetch(apiBaseUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(noteData)
    });
    return response.json();
}

async function updateNote(id, noteData) {
    const response = await fetch(`${apiBaseUrl}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(noteData)
    });
    return response.json();
}

async function deleteNote(id) {
    if (!confirm('Are you sure you want to delete this note?')) return;

    try {
        await fetch(`${apiBaseUrl}/${id}`, { method: 'DELETE' });
        fetchNotes();
    } catch (error) {
        console.error('Error deleting note:', error);
    }
}

function renderNotes(notes) {
    if (notes.length === 0) {
        emptyState.style.display = 'block';
        notesGrid.innerHTML = '';
        notesGrid.appendChild(emptyState);
        return;
    }

    emptyState.style.display = 'none';
    notesGrid.innerHTML = '';

    notes.forEach(note => {
        const date = new Date(note.createdAt).toLocaleDateString(undefined, {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });

        const card = document.createElement('div');
        card.className = 'note-card';
        card.innerHTML = `
            <h3>${note.title}</h3>
            <p>${note.content}</p>
            <div class="note-footer">
                <span>${date}</span>
                <div class="actions">
                    <button class="action-btn" onclick="editNote(${note.id}, '${note.title.replace(/'/g, "\\'")}', '${note.content.replace(/'/g, "\\'")}')">
                        <i data-lucide="edit-3"></i>
                    </button>
                    <button class="action-btn delete" onclick="deleteNote(${note.id})">
                        <i data-lucide="trash-2"></i>
                    </button>
                </div>
            </div>
        `;
        notesGrid.appendChild(card);
    });

    // Re-initialize Lucide icons for new elements
    if (window.lucide) {
        window.lucide.createIcons();
    }
}

function openModal(title, id = '', noteTitle = '', noteContent = '') {
    modalTitle.textContent = title;
    document.getElementById('note-id').value = id;
    document.getElementById('title').value = noteTitle;
    document.getElementById('content').value = noteContent;

    isEditing = id !== '';
    modalOverlay.style.display = 'flex';
    document.body.style.overflow = 'hidden'; // Prevent scroll
}

function closeModal() {
    modalOverlay.style.display = 'none';
    document.body.style.overflow = 'auto';
    noteForm.reset();
}

window.editNote = (id, title, content) => {
    openModal('Edit Note', id, title, content);
};
