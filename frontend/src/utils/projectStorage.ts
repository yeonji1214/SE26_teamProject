import type { Project } from "../types/project";

const CURRENT_PROJECT_KEY = "issue-tracker-current-project";
export const PROJECT_CHANGE_EVENT = "issue-tracker-project-change";

function notifyProjectChange(): void {
  window.dispatchEvent(new Event(PROJECT_CHANGE_EVENT));
}

export function saveCurrentProject(project: Project): void {
  localStorage.setItem(CURRENT_PROJECT_KEY, JSON.stringify(project));
  notifyProjectChange();
}

export function getCurrentProject(): Project | null {
  const storedProject = localStorage.getItem(CURRENT_PROJECT_KEY);

  if (!storedProject) {
    return null;
  }

  try {
    return JSON.parse(storedProject) as Project;
  } catch {
    localStorage.removeItem(CURRENT_PROJECT_KEY);
    return null;
  }
}

export function clearCurrentProject(): void {
  localStorage.removeItem(CURRENT_PROJECT_KEY);
  notifyProjectChange();
}